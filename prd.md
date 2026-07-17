# PRD: DailyGrind — Daily Routine Tracker (Android)

## 1. Overview
A single-screen Android app to track a fixed daily routine checklist (study/dev/health blocks), add a short note per item, write a 2-line "what I did today" summary, and export the whole card as an image to share on Instagram. New-gen UI: dark/neon glassmorphic theme, smooth slide/fade transitions, minimal color palette.

## 2. Goals
- Log daily discipline in under 60 seconds.
- Make the log look good enough to post as-is (no external editing needed).
- Zero friction: open app → tick boxes → tap download → share.

## 3. Target User
Just you (Rain) — a self-tracking tool for study/job-prep/dev routine, personal Instagram posting.

## 4. Core Checklist Items (fixed list, editable later)
| # | Item | Target Duration |
|---|------|------------------|
| 1 | DSA | 2 hrs |
| 2 | Development | 1.5 hrs |
| 3 | Database | 1 hr |
| 4 | Aptitude | 1 hr |
| 5 | Subjects (CN / OS / OOPS / System Design — any 2) | 2 hrs |
| 6 | Applied for jobs | — |
| 7 | Walk | 1 hr |
| 8 | Slept before 2 AM | — |
| 9 | Woke up before 8:30 AM | — |
| 10 | Screen time < 3 hrs | — |

Each row = **1 checkbox** (done ✅ / not done ❌ toggle) + **1 short note input** (single-line text, optional, e.g. "solved 4 leetcode mediums").

## 5. "What I Did" Section
- Free text area, capped at **2 bullet points**.
- Rendered in **white text** on the dark card for contrast/readability in the exported image.
- Each bullet auto-prefixed with a small dash/dot marker as user types and hits enter (max 2 lines, third attempt is blocked/disabled).

## 6. Export / Share Feature
- **Download button pinned at the top** of the screen (sticky, always visible).
- Tapping it renders the **entire current state of the card** (checklist + notes + what-I-did bullets + date) into a single PNG image.
- Image saved to device gallery via Android `MediaStore` API (no storage permission needed on API 29+).
- Optional: direct "Share to Instagram" intent (`Intent.ACTION_SEND`, type `image/png`, target `com.instagram.android`) right after generation, with fallback to generic share sheet if Instagram isn't installed.
- Export format: 1080x1350 (Instagram portrait post ratio) or 1080x1920 (story ratio) — toggle in export dialog.
- Export should capture exactly what's on screen (WYSIWYG) — implement via `Compose`'s `graphicsLayer` capture / `View.drawToBitmap()`.

## 7. Night Mode
- Toggle switch (moon/sun icon) in top bar.
- Two themes:
  - **Light (default)**: off-white background (#F6F6F8), deep-cyan accent, glassmorphic cards with soft shadow.
  - **Dark (toggle)**: near-black background (#0B0B10), neon accent (electric cyan/violet), glassmorphic cards (semi-transparent + blur).
- App opens in light mode on first launch; theme choice persists across sessions (DataStore/SharedPreferences).
- Export always reflects whichever theme is currently active — no forced lock to one mode.

## 8. UI/UX Design Direction
**Aesthetic:** simple, new-gen, minimal — not cluttered. Think Notion/Linear-level restraint but with a neon dark-mode edge.

- **Color scheme:** 1 background shade + 1 accent color + white/off-white text. No more than 2-3 colors total. Accent used only for: checked state, active toggle, download button, progress ring.
- **Buttons:** Rounded-pill or rounded-rect (16-20dp radius), flat fill with subtle glow on accent buttons, scale-down micro-interaction on press (0.96x, 100ms).
- **Checkboxes:** Custom-drawn, not default Android checkbox — circular or squircle, fills with accent color + checkmark draw-on animation (150-200ms) when tapped, subtle shake/red flash if marked "wrong/not done" (optional).
- **Transitions:**
  - Item rows: staggered fade + slide-up on screen load (60-80ms stagger per row).
  - Note input expand: height-animate open/close (AnimatedVisibility, spring spec).
  - Night mode toggle: cross-fade background + color animate (300ms).
  - Download button: ripple + brief scale-pulse on tap, then a bottom sheet slides up showing export preview.
- **Progress indicator:** optional top progress ring/bar showing X/10 items completed, animates as boxes are ticked.
- **Typography:** one clean sans-serif (e.g. Inter / Manrope), bold for item names, regular for notes.

## 9. Screens
1. **Main/Home Screen** — date header, sticky download button, checklist (10 rows), "What I Did" section at bottom, night mode toggle in top bar.
2. **Export Preview (bottom sheet/dialog)** — shows rendered image, aspect ratio toggle (post/story), confirm to save/share.
3. *(Optional future)* History screen — past days' saved cards, streak counter.

## 10. Data Model (local only, no backend needed for v1)
```
DailyLog {
  date: String (yyyy-MM-dd)
  items: List<ChecklistItem>
  whatIDid: List<String> (max 2)
  theme: String ("dark" | "light")
}

ChecklistItem {
  id: Int
  label: String
  isChecked: Boolean
  note: String
}
```
- Store via Room DB (local SQLite) keyed by date — auto-resets fresh checklist each new day, but keeps history for streaks later.

## 11. Tech Stack (recommended)
- **Language:** Kotlin
- **UI:** Jetpack Compose (best fit for the animation/transition requirements)
- **Animation:** Compose `animate*AsState`, `AnimatedVisibility`, `graphicsLayer` for capture
- **Local storage:** Room DB + DataStore (for theme/preferences)
- **Image export:** Compose `drawToBitmap` / `graphicsLayer` capture → `MediaStore` save
- **Share:** Android `Intent.ACTION_SEND`
- **Min SDK:** 26 (Android 8.0+) — covers 95%+ devices, keeps Compose features available

## 12. Non-Functional Requirements
- Cold start < 1.5s.
- All animations 60fps, no jank on mid-range devices.
- Works fully offline (no network permission needed for v1).
- App size target < 15MB.

## 13. Out of Scope (v1)
- Cloud sync / login
- Editable checklist item list (fixed list for now, hardcoded)
- Analytics/streak charts (future v2)
- iOS version

## 14. Future Scope (v2+)
- Custom/editable checklist items
- Weekly/monthly streak view + calendar heatmap
- Auto-post to Instagram Stories directly (Stories API) instead of manual share
- Widget for home screen showing today's progress ring
