# Asset Licenses

更新日期：2026-06-17

This document summarizes production asset sources. The machine-checked target list and checksums live in `tools/assets/asset_manifest.json`.

## Fonts

### Noto Sans TC

- Target: `app/src/main/res/font/noto_sans_tc.ttf`
- Source: Google Fonts repository, `ofl/notosanstc/NotoSansTC[wght].ttf`
- License: SIL Open Font License 1.1
- Use: default body/UI font.

### Noto Serif CJK TC Regular

- Target: `app/src/main/res/font/noto_serif_cjk_tc_regular.otf`
- Source: Noto CJK repository, `Serif/OTF/TraditionalChinese/NotoSerifCJKtc-Regular.otf`
- License: SIL Open Font License 1.1
- Use: headings, brand-like titles, classical text emphasis.

## Icons

### Material Design Icons

- Targets:
  - `ic_menu_24.xml`
  - `ic_settings_24.xml`
  - `ic_arrow_back_24.xml`
  - `ic_arrow_forward_24.xml`
  - `ic_check_24.xml`
  - `ic_favorite_24.xml`
  - `ic_favorite_border_24.xml`
  - `ic_today_24.xml`
  - `ic_auto_awesome_24.xml`
  - `ic_history_24.xml`
  - `ic_menu_book_24.xml`
  - `ic_person_24.xml`
- Source: Google Material Design Icons repository.
- License: Apache License 2.0.
- Use: toolbar, bottom navigation, favorite state, action affordances.

### Project-Authored Vectors

- Targets:
  - `ic_coins_24.xml`
  - `ic_yarrow_24.xml`
  - `ic_simple_hexagram_24.xml`
  - `ic_ritual_focus.xml`
- Source: authored in this repository.
- License: project-owned.
- Use: divination method cards and ritual focus state.

## Textures

### ambientCG Paper001

- Targets:
  - `app/src/main/res/drawable-nodpi/bg_paper_texture_light.webp`
  - `app/src/main/res/drawable-nodpi/bg_paper_texture_dark.webp`
- Source: `https://ambientCG.com/get?file=Paper001_1K-JPG.zip`
- License: CC0 1.0 Universal.
- Use: light/night page background texture through `@drawable/bg_page`.

## Artwork

### The Met: Scholar Viewing A Waterfall

- Target: `app/src/main/res/drawable-nodpi/art_scholar_waterfall.webp`
- Source object: The Metropolitan Museum of Art object 40086, Ma Yuan, `Scholar viewing a waterfall`.
- Source image: `https://images.metmuseum.org/CRDImages/as/web-large/DP154090.jpg`
- License/status: Public Domain / The Met Open Access.
- Use: onboarding artwork.

## Explicit Non-Sources

The Stitch export under `design/stitch_export/` remains reference material only. Production assets must not be copied from `lh3.googleusercontent.com/aida-public/...` URLs because their redistribution license is not verified in this repo.
