### UI Overhaul Implementation Plan

Here is the list of steps we will take to align the Android app's UI with the iOS version:

*   **[x] Task 1: Replicate the Dynamic Gradient Background**
    *   **Requirement:** The app's background must be a `LinearGradient` that changes its colors based on the time of day, mimicking the `gradientColors` logic from the iOS app.
    *   **Fulfillment:**
        1.  A new Composable function will be created that calculates the correct gradient colors based on the current hour.
        2.  This function will return a `Brush.linearGradient`.
        3.  The `MainScreen.kt` will be updated to use this dynamic gradient as its background.

*   **[x] Task 2: Redesign the Main Screen Layout (Cards & Structure)**
    *   **Requirement:** The main screen must be restructured to use a card-based layout. Each logical section (UV, Vitamin D, Settings) will be presented as a distinct card with rounded corners and a semi-transparent background.
    *   **Fulfillment:**
        1.  The layout in `MainScreen.kt` will be refactored into a vertically scrollable `Column`.
        2.  Each UI section will be encapsulated in its own Composable function.
        3.  These Composables will be wrapped in a `Surface` or `Card` with a `Modifier` that applies a `RoundedCornerShape`, a background color of `Color.Black.copy(alpha = 0.2f)`, and appropriate padding.
        4.  The sections will be ordered to match the iOS app: Header, UV, Vitamin D, Exposure Toggle, and Settings.

*   **[x] Task 3: Replicate Typography and Header**
    *   **Requirement:** The typography throughout the app must be updated to match the style and hierarchy of the iOS version, including the main "SUN DAY" header.
    *   **Fulfillment:**
        1.  A new `Text` Composable for the "SUN DAY" header will be created with a large font size and bold weight.
        2.  All other `Text` Composables will be reviewed and their `fontSize` and `fontWeight` adjusted to create a clear visual hierarchy (e.g., a very large UV index, smaller descriptive labels).

*   **[x] Task 4: Overhaul the Settings Section**
    *   **Requirement:** The current settings UI will be replaced with three distinct, styled buttons for Clothing, Sunscreen, and Skin Type. Tapping each button must open a modal bottom sheet containing the picker for that setting.
    *   **Fulfillment:**
        1.  The existing `SettingsPicker` will be removed.
        2.  Three new button Composables will be created and styled to look like the cards in the iOS app.
        3.  A `ModalBottomSheetLayout` will be implemented to host the picker content.
        4.  New Composables for `ClothingPicker`, `SunscreenPicker`, and `SkinTypePicker` will be created to be displayed within the bottom sheet. The `SkinTypePicker` will be detailed, including a color swatch and descriptive text, to match the iOS implementation.

*   **[x] Task 5: Replicate Icons**
    *   **Requirement:** The icons in the Android app must be updated to match the style of the SF Symbols used in the iOS app.
    *   **Fulfillment:**
        1.  The SF Symbols from the iOS app will be identified.
        2.  The closest available Material Design Icons will be selected from `androidx.compose.material.icons`.
        3.  All `Icon` Composables will be updated to use these new icons.