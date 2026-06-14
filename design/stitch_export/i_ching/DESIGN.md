---
name: I CHING
colors:
  surface: '#fbf9f8'
  surface-dim: '#dbdad9'
  surface-bright: '#fbf9f8'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f5f3f3'
  surface-container: '#efeded'
  surface-container-high: '#e9e8e7'
  surface-container-highest: '#e4e2e2'
  on-surface: '#1b1c1c'
  on-surface-variant: '#444748'
  inverse-surface: '#303031'
  inverse-on-surface: '#f2f0f0'
  outline: '#747878'
  outline-variant: '#c4c7c7'
  surface-tint: '#5f5e5e'
  primary: '#000000'
  on-primary: '#ffffff'
  primary-container: '#1c1b1b'
  on-primary-container: '#858383'
  inverse-primary: '#c8c6c5'
  secondary: '#7c5715'
  on-secondary: '#ffffff'
  secondary-container: '#ffcc7f'
  on-secondary-container: '#795512'
  tertiary: '#000000'
  on-tertiary: '#ffffff'
  tertiary-container: '#1b1c1a'
  on-tertiary-container: '#848481'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#e5e2e1'
  primary-fixed-dim: '#c8c6c5'
  on-primary-fixed: '#1c1b1b'
  on-primary-fixed-variant: '#474746'
  secondary-fixed: '#ffddaf'
  secondary-fixed-dim: '#f0be72'
  on-secondary-fixed: '#281800'
  on-secondary-fixed-variant: '#614000'
  tertiary-fixed: '#e4e2df'
  tertiary-fixed-dim: '#c8c6c4'
  on-tertiary-fixed: '#1b1c1a'
  on-tertiary-fixed-variant: '#474745'
  background: '#fbf9f8'
  on-background: '#1b1c1c'
  surface-variant: '#e4e2e2'
typography:
  headline-xl:
    fontFamily: Noto Serif
    fontSize: 40px
    fontWeight: '600'
    lineHeight: 48px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Noto Serif
    fontSize: 32px
    fontWeight: '500'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Noto Serif
    fontSize: 28px
    fontWeight: '500'
    lineHeight: 36px
  headline-md:
    fontFamily: Noto Serif
    fontSize: 24px
    fontWeight: '500'
    lineHeight: 32px
  body-lg:
    fontFamily: Noto Sans
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Noto Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-md:
    fontFamily: Noto Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.05em
  caption:
    fontFamily: Noto Sans
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  unit: 8px
  container-margin: 24px
  gutter: 16px
  stack-sm: 12px
  stack-md: 24px
  stack-lg: 48px
---

## Brand & Style
The brand personality is rooted in the "Zen-inspired" intersection of ancient wisdom and modern mindfulness. It avoids the cluttered, mystical aesthetic of traditional divination apps in favor of a clean, editorial approach akin to a high-end journaling or meditation platform. The emotional response should be one of immediate calm, clarity, and intentionality.

The design style is **Minimalist with a Tactile Ink-Wash influence**. It leverages heavy whitespace (reminiscent of *Ma* in Japanese design) to allow the content—hexagrams and interpretations—to breathe. The interface mimics the textural quality of handmade rice paper (*Washi*) and the fluidity of sumi-e ink, while maintaining the rigorous precision of a professional SaaS product.

## Colors
The palette is inspired by traditional ink-wash paintings on aged parchment. 
- **Canvas/Background:** Use `#FBF9F6` as the primary background to simulate raw paper.
- **Ink/Primary:** `#1A1A1A` provides high-contrast legibility for all primary text and hexagram iconography.
- **Accent Gold:** Use `#A67C37` (Light) and `#C6A15B` (Dark) sparingly for highlights, active states, or signifying "Changing Lines" within a hexagram.
- **Borders & Dividers:** Use the subtle `#D8D2C8` for hairline separators to maintain structure without breaking the visual flow.

## Typography
The typography pairing establishes an "Editorial Zen" hierarchy. 
- **Headlines:** Noto Serif is used for Hexagram names and section titles to convey authority and historical weight. The `headline-xl` should be reserved for the primary hexagram result.
- **Body:** Noto Sans provides a clean, neutral counterpoint for long-form interpretations and UI labels, ensuring maximum readability during deep reading sessions.
- **Vertical Rhythm:** Maintain generous line heights (1.5x - 1.6x) for body text to evoke the feeling of a printed book.

## Layout & Spacing
The design system utilizes a **Fixed Grid** for desktop (max-width 1200px) and a **Fluid Content Column** for mobile. 
- **Margins:** A generous 24px side margin on mobile ensures content doesn't feel cramped.
- **Rhythm:** Spacing follows an 8px incremental scale. Large vertical gaps (`stack-lg`) are encouraged between major sections (e.g., the Hexagram visual and its Judgment text) to facilitate a meditative reading pace.
- **Alignment:** Central alignment is preferred for results screens to create a "focal point," while left-alignment is used for lists and settings to ensure utility.

## Elevation & Depth
Depth is created through **Tonal Layering** rather than traditional shadows.
- **Surfaces:** Use `Surface (#F0EDE7)` for secondary cards or content containers against the `Canvas` background. This creates a subtle "stacked paper" effect.
- **Borders:** Instead of drop shadows, use 1px solid borders (`#D8D2C8`) to define boundaries.
- **Overlays:** For modals or menus, use a Backdrop Blur (12px) with a semi-transparent tinted overlay to maintain a sense of environmental continuity.

## Shapes
The shape language combines the organic nature of calligraphy with the precision of modern UI.
- **Pill Shapes:** Used for all primary actions and chips to provide a soft, welcoming touch that contrasts with the geometric hexagrams.
- **Soft Corners:** Large cards and containers use a 20px radius (`rounded-xl` in this system) to evoke the feel of smooth river stones or rounded paper edges.

## Components
- **Buttons:** Primary buttons are pill-shaped, using high-contrast fills (`Ink` background with `Canvas` text). Secondary buttons use an outlined style with a 1px border.
- **Hexagram Icons:** These should be rendered with a slight "ink bleed" or varied stroke weight to feel hand-drawn rather than digitally perfect.
- **Cards:** Content blocks (e.g., "Daily Reflection") should use the `Surface` color with 20px rounded corners and no shadow.
- **Chips/Labels:** Small, pill-shaped tags used for identifying Trigrams (e.g., "Thunder," "Lake") using `Muted` text on a `Surface` background.
- **Progress Indicators:** When "casting" coins or stalks, use an organic, slow-filling ink-wash animation rather than a standard spinning loader.
- **Inputs:** Clean, bottom-border only lines for text entry to keep the interface as "paper-like" as possible.