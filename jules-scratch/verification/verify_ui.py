from playwright.sync_api import sync_playwright, Page, expect

def run():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()

        try:
            # Navigate to the running frontend application
            page.goto("http://localhost:3000/", timeout=30000)

            # Fill in the login form
            page.get_by_placeholder("Email").fill("admin@hr-assistant.com")
            page.get_by_placeholder("Пароль").fill("password")

            # Click the login button
            page.get_by_role("button", name="Войти").click()

            # Wait for navigation to the dashboard
            dashboard_heading = page.get_by_role("heading", name="Dashboard")
            expect(dashboard_heading).to_be_visible(timeout=15000)

            # Give the page a moment to ensure all animations have started.
            page.wait_for_timeout(2000)

            # Take a screenshot
            page.screenshot(path="jules-scratch/verification/ui_screenshot.png")
            print("Screenshot of dashboard taken successfully.")

        except Exception as e:
            print(f"An error occurred: {e}")
            page.screenshot(path="jules-scratch/verification/error_screenshot.png")
            print("Error screenshot taken.")

        finally:
            browser.close()

if __name__ == "__main__":
    run()
