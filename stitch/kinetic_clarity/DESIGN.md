---
name: Kinetic Clarity
colors:
  surface: '#fcf8fb'
  surface-dim: '#dcd9dc'
  surface-bright: '#fcf8fb'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f6f3f5'
  surface-container: '#f0edef'
  surface-container-high: '#eae7ea'
  surface-container-highest: '#e4e2e4'
  on-surface: '#1b1b1d'
  on-surface-variant: '#594139'
  inverse-surface: '#303032'
  inverse-on-surface: '#f3f0f2'
  outline: '#8d7168'
  outline-variant: '#e1bfb5'
  surface-tint: '#ab3500'
  primary: '#ab3500'
  on-primary: '#ffffff'
  primary-container: '#ff6b35'
  on-primary-container: '#5f1900'
  inverse-primary: '#ffb59d'
  secondary: '#8d4f00'
  on-secondary: '#ffffff'
  secondary-container: '#fe9824'
  on-secondary-container: '#663800'
  tertiary: '#00677e'
  on-tertiary: '#ffffff'
  tertiary-container: '#00a7cb'
  on-tertiary-container: '#003744'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffdbd0'
  primary-fixed-dim: '#ffb59d'
  on-primary-fixed: '#390c00'
  on-primary-fixed-variant: '#832600'
  secondary-fixed: '#ffdcc0'
  secondary-fixed-dim: '#ffb875'
  on-secondary-fixed: '#2d1600'
  on-secondary-fixed-variant: '#6b3b00'
  tertiary-fixed: '#b5ebff'
  tertiary-fixed-dim: '#59d5fb'
  on-tertiary-fixed: '#001f28'
  on-tertiary-fixed-variant: '#004e60'
  background: '#fcf8fb'
  on-background: '#1b1b1d'
  surface-variant: '#e4e2e4'
  surface-gray: '#F2F2F7'
  glass-white: rgba(255, 255, 255, 0.7)
  success-green: '#34C759'
  danger-red: '#FF3B30'
typography:
  display-lg:
    fontFamily: Hanken Grotesk
    fontSize: 40px
    fontWeight: '700'
    lineHeight: 48px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Hanken Grotesk
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-lg-mobile:
    fontFamily: Hanken Grotesk
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 34px
  headline-md:
    fontFamily: Hanken Grotesk
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 30px
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0.01em
  label-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.05em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 8px
  margin-mobile: 20px
  gutter-md: 16px
  stack-sm: 4px
  stack-md: 12px
  stack-lg: 24px
---

## Brand & Style

This design system translates a minimalist personal portfolio aesthetic into a high-utility mobile voice accounting context. The brand personality is **articulate, efficient, and sophisticated**, designed to evoke a sense of financial control through extreme visual clarity. 

The design style is **Modern Minimalist with Glassmorphic Accents**. It relies on a foundation of generous white space and high-contrast typography, punctuated by vibrant, energetic accents. To reflect the "voice" aspect of the app, the UI utilizes translucent layers and soft background blurs to suggest a fluid, atmospheric interface that feels responsive to audio input. The goal is to move away from traditional "bank-like" rigidity toward a more personal, lifestyle-integrated tool.

## Colors

The palette is anchored by a high-energy **Sunset Primary (#FF6B35)** and a supporting **Amber Secondary (#F7931E)**. These warm tones are used sparingly for calls-to-action, recording indicators, and financial growth visualizations. 

The interface relies heavily on a "layered white" approach: **#FFFFFF** for primary cards and **#F2F2F7** for background surfaces to create subtle depth without heavy borders. Text and iconography utilize **#1C1C1E** to ensure maximum legibility and a premium feel. For voice-active states, use a gradient transition between the primary and secondary colors to signify motion and processing.

## Typography

The typography system is built for rapid scanning of financial data. **Hanken Grotesk** is used for all headlines and numerical displays, providing a sharp, contemporary "tech-forward" feel that mirrors the precision of accounting. 

**Inter** handles all body copy and functional labels, chosen for its exceptional legibility at small sizes on mobile screens. For large currency amounts, use the `display-lg` style with a slightly tighter letter spacing to create a distinctive visual anchor on the dashboard.

## Layout & Spacing

The layout utilizes a **fluid grid** optimized for mobile interaction. A standard **20px margin** is maintained on the left and right edges of the screen to ensure content does not feel cramped. 

Vertical rhythm is established using an **8px base unit**. Elements are grouped in "stacks" with 12px or 24px spacing to clearly differentiate between related data points (like an expense category and its amount) and separate functional sections (like the transaction list vs. the navigation bar). Large dashboard summaries should utilize a 32px top padding to create a "breathable" entry point for the user.

## Elevation & Depth

This system avoids heavy drop shadows in favor of **Tonal Layers** and **Glassmorphism**. 

1.  **Level 0 (Background):** Flat `#F2F2F7`.
2.  **Level 1 (Cards/Lists):** Pure `#FFFFFF` with a very soft, 4% opacity neutral shadow (0px 4px 20px) to give a slight lift.
3.  **Level 2 (Active States/Modals):** Glassmorphic surfaces using `glass-white` with a 20px backdrop blur. This is specifically reserved for the voice-input overlay and floating action buttons.
4.  **Level 3 (Urgent/Action):** Primary color surfaces with no shadow, relying on pure chromatic contrast to sit "above" the interface.

## Shapes

The shape language is **Refined and Rounded**. A standard **0.5rem (8px)** corner radius is used for input fields and small cards. 

Larger containers, such as the primary dashboard expense card or the bottom sheet drawer, utilize a more pronounced **1rem (16px)** radius to feel softer and more approachable. Buttons should follow the `rounded-xl` logic (24px) to create a "pill" appearance that invites touch.

## Components

### Buttons
Primary buttons are pill-shaped with the Sunset Primary gradient. Secondary buttons use a `surface-gray` background with neutral text. "Voice" buttons are circular, featuring a subtle pulse animation when active.

### Cards
Transaction cards use a white background with 16px padding. Icons within cards are housed in soft-colored circles (10% opacity of the category's color) to provide visual categorization without clutter.

### Input Fields (Voice & Text)
Voice input is represented by a persistent bottom bar that expands into a glassmorphic overlay. When text input is required, fields are borderless with a subtle `surface-gray` fill, transitioning to a primary-colored bottom border on focus.

### Chips & Tags
Used for expense categories. They feature a `label-sm` font, high-radius corners, and low-saturation backgrounds that match the category's theme.

### Lists
Lists are "clean," meaning no dividers between items. Instead, 12px of vertical spacing provides enough separation, keeping the interface light and modern.