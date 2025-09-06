import os
from playwright.sync_api import sync_playwright, Page, expect

# Get the directory of the script
script_dir = os.path.dirname(os.path.abspath(__file__))
screenshot_path = os.path.join(script_dir, "landing_page.png")


def run(playwright):
    browser = playwright.chromium.launch(headless=True)
    page = browser.new_page()

    try:
        # Navigate to the landing page
        page.goto("http://localhost:3000/", timeout=20000)

        # Wait for the "Start Interview" button to be visible.
        button = page.get_by_role("link", name="Start Interview")
        expect(button).to_be_visible(timeout=15000)

        # Take a screenshot
        page.screenshot(path=screenshot_path)
        print(f"Screenshot saved to {screenshot_path}")

    except Exception as e: # Catch ANY exception
        print("An exception occurred. Here is the page content:")
        print("==============================================")
        print(page.content())
        print("==============================================")
        raise e
    finally:
        browser.close()


with sync_playwright() as playwright:
    run(playwright)
