# DESIGN.md — DailyGrind Visual & Interaction Spec

## 1. Design Philosophy
Minimal, dark-first, neon-glass aesthetic. One accent color does all the work — everything else is background, text, and glass. No decorative clutter. Every element on screen either tracks progress or lets you act on it. The exported image should look like something you'd actually want on your Instagram story, not a screenshot of a form.

---

## 2. Color System

### Light Mode (default)
| Token | Hex | Usage |
|---|---|---|
| `bg-base` | `#F6F6F8` | Screen background |
| `bg-surface` | `#FFFFFF` | Card background |
| `bg-surface-glass` | `rgba(255,255,255,0.7)` + backdrop blur 20px | Checklist row cards |
| `accent` | `#0091AD` (deep cyan, AA-contrast on white) | Checked state, active toggle, buttons, progress ring |
| `accent-alt` | `#7A4FE0` (violet) | Secondary highlight — download button gradient end |
| `danger` | `#E0405C` | "Not done" flash state |
| `text-primary` | `#14141A` | Item labels, "What I Did" bullets |
| `text-secondary` | `#6B6B78` | Notes, timestamps, placeholder text |
| `divider` | `rgba(0,0,0,0.08)` | Row separators |

### Dark Mode (toggle)
| Token | Hex | Usage |
|---|---|---|
| `bg-base` | `#0B0B10` | Screen background |
| `bg-surface` | `#15151C` | Card background (base, before glass blur) |
| `bg-surface-glass` | `rgba(21,21,28,0.55)` + blur 20px | Checklist row cards |
| `accent` | `#6FE3FF` (electric cyan) | Same roles as light |
| `accent-alt` | `#B98CFF` | Same roles as light |
| `danger` | `#FF5C7A` | Same |
| `text-primary` | `#FFFFFF` | Labels, "What I Did" bullets |
| `text-secondary` | `#9A9AA8` | Notes |
| `divider` | `rgba(255,255,255,0.08)` | Row separators |

Only 2 accent colors + base neutrals across both themes — kept deliberately tight per the "simple color scheme" requirement. App opens in **light mode by default**; dark mode is opt-in via the top-bar toggle and persists once switched.

---

## 3. Typography
- **Font:** Manrope (fallback: Inter, then system default)
- **Scale:**
  | Style | Size | Weight | Usage |
  |---|---|---|---|
  | Display | 22sp | Bold (700) | Date header ("Fri, 17 Jul") |
  | Title | 16sp | SemiBold (600) | Checklist item label |
  | Body | 14sp | Regular (400) | Note input text |
  | Caption | 12sp | Medium (500) | Duration tag ("2 hrs"), placeholder text |
  | Bullet | 15sp | Regular (400) | "What I Did" lines |

---

## 4. Layout & Spacing
- **Base unit:** 4dp grid.
- Screen horizontal padding: **16dp**
- Card vertical padding: **12dp**, internal row gap: **8dp**
- Checklist row height: auto, min **56dp**
- Gap between rows: **10dp**
- Corner radius: **20dp** (cards), **14dp** (rows), **50%/pill** (buttons)
- Sticky top bar height: **56dp**, elevated with blur-through background when scrolled

---

## 5. Screen Layout — Home

```
┌─────────────────────────────────────────┐
│ [☰ / date]     DailyGrind      [⬇] [🌙] │  ← sticky top bar
├─────────────────────────────────────────┤
│  ●●●●●●○○○○  6/10 completed              │  ← progress ring/bar, animated
├─────────────────────────────────────────┤
│  ┌─────────────────────────────────┐    │
│  │ ☑  DSA                  2 hrs   │    │  ← checklist row (glass card)
│  │    ✎ "solved 4 mediums"         │    │  ← note input, collapsed/expanded
│  └─────────────────────────────────┘    │
│  ┌─────────────────────────────────┐    │
│  │ ☐  Development           1.5hr  │    │
│  └─────────────────────────────────┘    │
│   ... (10 rows total) ...                │
├─────────────────────────────────────────┤
│  WHAT I DID TODAY                        │
│  • shipped the export feature            │
│  • [+ add second point]                  │
└─────────────────────────────────────────┘
```

- Top bar is **sticky/pinned**, stays visible on scroll (background gains blur+opacity once content scrolls under it).
- Download icon button (⬇) always leftmost in the action cluster; night mode toggle (🌙/☀) next to it.
- Progress bar directly under top bar, full width, thin (6dp), rounded ends, fills left→right with accent gradient as items are checked (spring-animated).

---

## 6. Component Specs

### 6.1 Checklist Row (Glass Card)
- Background: `bg-surface-glass`, 1px border `rgba(255,255,255,0.06)`
- Left: custom checkbox (28dp squircle)
- Center: label (Title style) + duration tag (Caption, pill badge, `accent` at 15% opacity fill)
- Right: chevron/pencil icon to expand note field
- Tap anywhere on row (outside checkbox) → expands note input inline (height-animates open)
- States:
  - **Unchecked:** squircle outline only, `text-secondary` border
  - **Checked (done):** squircle fills `accent`, white checkmark path draws in (stroke animation, 180ms, ease-out)
  - **Marked wrong/not done** *(long-press or secondary tap toggles this state)*: squircle fills `danger` at low opacity, brief 2px horizontal shake (3 cycles, 250ms total), red border pulse then settles

### 6.2 Note Input (per row)
- Single-line `TextField`, transparent background, bottom-border only (1px `divider`, becomes `accent` on focus)
- Placeholder: "add a short note…" in `text-secondary`
- Max length: 60 characters (keeps export image clean)
- Collapses back to icon-only if left empty on blur

### 6.3 "What I Did" Block
- Section label: Caption style, uppercase, letter-spacing 0.5, `text-secondary`
- 2 bullet lines max, each a `TextField` with a leading dash (–) rendered in `accent`
- Text itself renders in `text-primary` — dark ink on light background by default, switching to white automatically when dark mode is active — always high-contrast in the exported image
- Second line only appears after first is non-empty (slide+fade in, 200ms) — enforces the 2-point cap structurally, no third line ever rendered
- Empty state: single ghost line "+ add what you did today"

### 6.4 Buttons
- **Primary (Download):** pill shape, gradient fill `accent → accent-alt` (45°), white icon, subtle outer glow (8dp blur, accent at 30% opacity), press state scales to 0.96 with 100ms spring
- **Icon buttons (night mode, etc.):** circular, 40dp, `bg-surface-glass` fill, icon in `text-primary`, no border
- **Secondary (dialog actions):** outline only, 1px `accent`, transparent fill, fills solid on press

### 6.5 Export Bottom Sheet
- Slides up from bottom (spring, damping ~0.85), rounded top corners 24dp, `bg-surface` solid (no glass here — needs to render cleanly for screenshot preview)
- Shows live preview of the export image at reduced scale
- Aspect ratio toggle: two pill segments — "Post 4:5" / "Story 9:16"
- Bottom row: "Save to Gallery" (secondary) / "Share to Instagram" (primary, gradient)

---

## 7. Motion & Animation Spec

| Interaction | Animation | Duration | Easing |
|---|---|---|---|
| Screen load | Rows fade + slide-up 12dp, staggered | 250ms/row, 60ms stagger | Ease-out |
| Checkbox toggle (done) | Fill scale-in + checkmark stroke draw | 180ms | Ease-out |
| Checkbox toggle (wrong) | Fill + horizontal shake | 250ms | Ease-in-out, 3 cycles |
| Note field expand/collapse | Height animate | 200ms | Spring (medium bounce) |
| Progress bar fill | Width animate | 300ms | Spring (low bounce) |
| Night mode switch | Background + all token colors cross-fade | 300ms | Ease-in-out |
| Download button press | Scale 1 → 0.96 → 1 | 100ms | Spring |
| Export sheet open | Slide up from bottom + scrim fade-in | 300ms | Spring (damping 0.85) |
| "What I Did" second bullet reveal | Slide-up + fade | 200ms | Ease-out |

General rule: nothing linear. Everything either eases or springs — this is the single biggest lever for the "new-gen app" feel the user asked for.

---

## 8. Export Image Design (the Instagram output)
This is a **separate, purpose-built layout** — not a raw screenshot — rendered off-screen at export time.

- Canvas: 1080×1350 (post) or 1080×1920 (story)
- Background: `bg-base` of whichever theme is active at export time, with a soft radial accent glow (low opacity, top-left corner) for visual interest
- Header: date, large, bold, in `text-primary`
- Checklist rendered as compact rows: checkmark/x icon + label + note (if present), no interactive chrome (no chevrons, no empty placeholders)
- Progress summary badge near top: "6/10 completed today" in a pill
- "What I Did" bullets rendered in larger white text near the bottom, as the visual focal point
- Small watermark/logo bottom-right corner (subtle, app name + accent-colored dot) — optional branding, low opacity
- No system UI, no buttons, no cursors — pure content

---

## 9. Accessibility
- Minimum tap target: 44×44dp for all interactive elements
- Text contrast: all `text-primary` on `bg-surface` combos meet WCAG AA (4.5:1) in both themes
- Checkbox states distinguishable by shape/icon, not color alone (checkmark vs. x-mark, not just cyan vs. red)

---

## 10. Icon Set
Simple line icons (1.5-2dp stroke), rounded caps — consistent with Feather/Lucide icon style:
- Checkbox: custom-drawn (not from icon set)
- Download: arrow-into-tray
- Night mode: moon / sun swap (cross-fade, not swap-cut)
- Pencil/chevron: note expand indicator
- Share: standard Android share glyph (in export sheet only)
