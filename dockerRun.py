import subprocess
import sys

COMPOSE_FILES = [
    "./docker-compose.yml",
    "monitoring/docker-compose.yml",
    "keyCloak/docker-compose.yml",
]

STATUS = {}

def is_running(compose_file):
    result = subprocess.run(
        ["docker", "compose", "-f", compose_file, "ps", "-q"],
        stdout=subprocess.PIPE,
        stderr=subprocess.DEVNULL,
        text=True
    )
    return bool(result.stdout.strip())


def print_status():
    print("\n📊 Current containers status:\n")
    for file, running in STATUS.items():
        state = "🟢 RUNNING" if running else "🔴 STOPPED"
        print(f" - {file} → {state}")


def confirm():
    print("\n⚠️  The following actions will be applied:")
    for file, running in STATUS.items():
        action = "STOP" if running else "START"
        print(f" - {file} → {action}")

    choice = input("\n👉 Do you want to continue? (y/N): ").strip().lower()
    return choice in ("y", "yes")


def down(compose_file):
    print(f"🛑 Stopping → {compose_file}")
    subprocess.run(
        ["docker", "compose", "-f", compose_file, "down"],
        check=True
    )


def up(compose_file):
    print(f"🚀 Starting → {compose_file}")
    subprocess.run(
        ["docker", "compose", "-f", compose_file, "up", "-d"],
        check=True
    )


def main():
    # 1️⃣ Check status first
    for compose_file in COMPOSE_FILES:
        STATUS[compose_file] = is_running(compose_file)

    # 2️⃣ Show status summary
    print_status()

    # 3️⃣ Ask for confirmation
    if not confirm():
        print("\n❌ Operation cancelled.")
        sys.exit(0)

    # 4️⃣ Apply actions
    for compose_file, running in STATUS.items():
        try:
            if running:
                down(compose_file)
            else:
                up(compose_file)
        except subprocess.CalledProcessError:
            print(f"❌ Error while processing {compose_file}")
            sys.exit(1)

    print("\n✅ All operations completed successfully.")


if __name__ == "__main__":
    main()
