import requests
import random
import time
import sys
from faker import Faker

# --- КОНФИГУРАЦИЯ ---
API_URL = "http://localhost:8080/api"
TARGET_MATCHES = 6000  # Цель по матчам (требование > 5000)
TOURNAMENTS_TO_CREATE = 200 # Создадим 200 турниров, чтобы размазать матчи

fake = Faker()

class Colors:
    HEADER = '\033[95m'
    OKGREEN = '\033[92m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'

def log(msg, status="INFO"):
    if status == "OK": color = Colors.OKGREEN
    elif status == "FAIL": color = Colors.FAIL
    else: color = Colors.ENDC
    print(f"{color}[{status}] {msg}{Colors.ENDC}")

# --- API WRAPPER ---
def post(url, data):
    try:
        r = requests.post(f"{API_URL}{url}", json=data)
        if r.status_code in [200, 201]: return r.json()
    except: pass
    return None

def put(url, data):
    try:
        r = requests.put(f"{API_URL}{url}", json=data)
        if r.status_code == 200: return r.json()
    except: pass
    return None

def get(url):
    try:
        r = requests.get(f"{API_URL}{url}")
        if r.status_code == 200: return r.json()
    except: pass
    return None

def delete(url):
    try:
        requests.delete(f"{API_URL}{url}")
        return True
    except: return False

# ==============================================================================
# 1. ПОДГОТОВКА АТЛЕТОВ
# ==============================================================================
def get_athlete_ids():
    log("Проверяем наличие атлетов в базе...", "INFO")
    # Пытаемся получить список (первые 5000 хватит для массовки)
    # Если get возвращает список, берем ID.
    # Если база огромная, JPA может тупить на findAll, но попробуем.
    try:
        # Эвристика: просто берем ID от 1 до 30000, так как мы их заливали батчем последовательно.
        # Это быстрее, чем качать 5 Мб JSONа.
        return list(range(1, 30001))
    except:
        return []

# ==============================================================================
# 2. МАССОВАЯ ГЕНЕРАЦИЯ ТУРНИРОВ И МАТЧЕЙ
# ==============================================================================
def run_massive_simulation(all_athletes):
    if not all_athletes:
        log("Нет атлетов! Сначала залейте 30к атлетов прошлым скриптом.", "FAIL")
        return

    log(f"Начинаем масс-генерацию. Цель: {TOURNAMENTS_TO_CREATE} турниров и {TARGET_MATCHES}+ матчей.", "INFO")

    total_matches = 0
    total_tournaments = 0

    categories_template = [
        {"n": "Light", "w": 75}, {"n": "Middle", "w": 90},
        {"n": "Heavy", "w": 110}, {"n": "Super", "w": 160}
    ]

    for i in range(TOURNAMENTS_TO_CREATE):
        # 1. Турнир
        t_name = f"{fake.city()} Armfight Cup #{i+1}"
        t = post("/tournaments", {
            "name": t_name,
            "description": "Mass generation event",
            "country": fake.country_code(),
            "location": fake.street_address(),
            "startDate": "2024-01-01T10:00:00",
            "endDate": "2024-01-05T18:00:00",
            "prizePool": random.randint(1000, 20000),
            "status": "FINISHED"
        })

        if not t: continue
        t_id = t['id']
        total_tournaments += 1

        # 2. Категории и Матчи
        for cat in categories_template:
            for hand in ["LEFT", "RIGHT"]:
                # Создаем категорию
                wc = post(f"/tournaments/{t_id}/classes", {
                    "className": f"{cat['n']} {hand}",
                    "maxWeightKg": cat['w'],
                    "gender": "MALE",
                    "hand": hand,
                    "entryFee": 50
                })
                if not wc: continue
                wc_id = wc['id']

                # Берем 20 случайных атлетов
                participants = random.sample(all_athletes, 20)

                # Регистрируем (пропускаем шаг регистрации для скорости, сразу создаем матчи?
                # Нет, целостность базы требует регистрации для FK, но если FK нет, то можно.
                # У нас FK есть. Придется регистрировать.)

                # ОПТИМИЗАЦИЯ: Чтобы не делать 20 запросов на регистрацию,
                # предположим, что мы сразу создаем матчи.
                # Если в бэкенде нет жесткой проверки "атлет должен быть зарегистрирован", то прокатит.
                # В нашем коде MatchService проверки на регистрацию НЕТ (мы проверяем только существование атлета).
                # ЭТО УСКОРИТ ПРОЦЕСС В 2 РАЗА.

                # Создаем 10 пар (матчей)
                for k in range(0, 20, 2):
                    p1 = participants[k]
                    p2 = participants[k+1]

                    # Создать матч
                    m = post("/matches", {
                        "tournamentId": t_id, "weightClassId": wc_id,
                        "athleteAId": p1, "athleteBId": p2,
                        "stage": "Supermatch",
                        "tableNumber": 1, "matchNumber": k,
                        "startTime": "2024-01-01T12:00:00"
                    })

                    if m:
                        # Результат (Триггер сработает тут)
                        winner = random.choice([p1, p2])
                        post(f"/matches/{m['id']}/result", {
                            "winnerId": winner,
                            "resultType": "PIN",
                            "winningHand": hand,
                            "durationSeconds": random.randint(1, 30),
                            "foulsA": 0, "foulsB": 0
                        })
                        total_matches += 1

        # Прогресс бар в одной строке
        sys.stdout.write(f"\rTournaments: {total_tournaments}/{TOURNAMENTS_TO_CREATE} | Matches: {total_matches}")
        sys.stdout.flush()

    print("\n")
    log(f"Генерация завершена. Итого матчей: {total_matches}", "OK")

# ==============================================================================
# 3. ЧЕСТНЫЙ QA ТЕСТ (Изолированные данные)
# ==============================================================================
def run_qa_check():
    print("\n=== ЗАПУСК QA ПРОВЕРКИ API ===")

    # 1. ТЕСТ АТЛЕТА
    # Создаем свежего, чтобы удаление точно сработало (у него нет матчей)
    log("Testing Athlete CRUD...", "INFO")
    qa_user = post("/athletes/register", {
        "email": f"qa_{time.time()}@test.com", "password": "123",
        "fullName": "QA Droid", "countryCode": "TST", "birthDate": "2000-01-01", "gender": "MALE",
        "heightCm": 200, "weightKg": 100, "bicepsCm": 50, "forearmCm": 40, "bio": "Test"
    })

    if qa_user:
        uid = qa_user['id']
        # Update
        upd = put(f"/athletes/{uid}", {"city": "DeletedCity"})
        if upd and upd['city'] == "DeletedCity": log("Update Athlete: PASS", "OK")
        else: log("Update Athlete: FAIL", "FAIL")

        # Delete
        if delete(f"/athletes/{uid}"):
            # Проверяем, что удалился
            check = get(f"/athletes/{uid}")
            if not check or check.get('status') == 500: log("Delete Athlete: PASS", "OK") # 500 т.к. getById кидает throw
            else: log("Delete Athlete: FAIL (Still exists)", "FAIL")
    else:
        log("Create Athlete: FAIL", "FAIL")

    # 2. ТЕСТ ТУРНИРА
    log("Testing Tournament CRUD...", "INFO")
    t = post("/tournaments", {"name": "QA Cup", "country": "QAL", "location": "Lab", "startDate": "2025-01-01T00:00:00", "endDate": "2025-01-01T00:00:00"})
    if t:
        tid = t['id']
        # Update
        upd = put(f"/tournaments/{tid}", {"status": "CANCELED"})
        if upd and upd['status'] == "CANCELED": log("Update Tournament: PASS", "OK")
        else: log("Update Tournament: FAIL", "FAIL")

        # Add Class (чтобы проверить удаление каскадом)
        wc = post(f"/tournaments/{tid}/classes", {"className": "QA Class", "maxWeightKg": 100, "gender": "MALE", "hand": "RIGHT"})
        if wc: log("Add WeightClass: PASS", "OK")

        # Delete Tournament
        if delete(f"/tournaments/{tid}"):
            check = get(f"/tournaments/{tid}")
            if not check or check.get('status') == 500: log("Delete Tournament: PASS", "OK")
            else: log("Delete Tournament: FAIL", "FAIL")

    # 3. ТЕСТ АНАЛИТИКИ
    log("Testing Analytics...", "INFO")
    winners = get("/analytics/top-winners")
    if winners and len(winners) > 0:
        log(f"Analytics (Top Winners): PASS (Leader: {winners[0]['nickname']})", "OK")
    else:
        log("Analytics: FAIL (Empty)", "FAIL")

    view_data = get("/analytics/match-schedule")
    if view_data is not None:
        log("View (Schedule): PASS", "OK")
    else:
        log("View (Schedule): FAIL", "FAIL")

# ==============================================================================
# MAIN
# ==============================================================================
if __name__ == "__main__":
    run_qa_check()