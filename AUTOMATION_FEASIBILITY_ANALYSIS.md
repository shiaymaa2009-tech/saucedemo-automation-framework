# SauceDemo Automation Feasibility & Test Traceability Reference
**Document Identifier:** `SD-AUTO-FEASIBILITY-V1.0`  
**Application Under Test:** [SauceDemo E-Commerce](https://www.saucedemo.com/)  
**Total Manual Test Cases:** 116 Cases (`manual-tests/SauceDemo_Manual_TestCases.xlsx`)  
**Target Browser:** Microsoft Edge | **Language:** Java 17 | **Runner:** TestNG 7.11+  

---

## 1. Automation Feasibility Summary

| Module Name | Total TCs | Automated Scope | Manual-Only Scope | Target Test Class |
|:---|:---:|:---:|:---:|:---|
| **Module 1: Authentication** | 22 | 10 Automated (45%) | 12 Manual | `LoginTest.java` |
| **Module 2: Inventory & 4-Way Sorting** | 18 | 7 Automated (39%) | 11 Manual | `InventoryTest.java` |
| **Module 3: Product Details** | 18 | 6 Automated (33%) | 12 Manual | `ProductDetailsTest.java` |
| **Module 4: Shopping Cart & Badge** | 18 | 9 Automated (50%) | 9 Manual | `ShoppingCartTest.java` |
| **Module 5: End-to-End Checkout** | 20 | 11 Automated (55%) | 9 Manual | `CheckoutE2ETest.java` |
| **Module 6: Logout & Session State** | 20 | 9 Automated (45%) | 11 Manual | `SessionLogoutTest.java` |
| **TOTAL** | **116** | **52 Automated (45%)** | **64 Manual (55%)** | **6 Dedicated Test Classes** |

*Note: High-ROI, deterministic functional, security, and state-transition cases are automated. Exploratory layout checks, physical mobile gestures, external social links, and visual-only CSS inspection cases are mapped for structured manual execution.*

---

## 2. Module 1: Authentication (`LoginTest.java`)
**Source Specification:** `TS-SD-LGN-001` (22 Test Cases)  
**Data Source:** `src/test/resources/testdata/LoginData.xlsx` (Apache POI) + `UserCredentials.java`

| Test ID | Test Scenario Title | Feasibility | Automation Method / Strategy | Assertion Criteria |
|:---|:---|:---:|:---|:---|
| `TC_LGN_001` | Valid Standard User Login | **Automated** | `testSuccessfulLoginStandardUser()` | URL contains `/inventory.html`; Header displays "Products". |
| `TC_LGN_002` | Locked-Out Account Enforcement | **Automated** | `testLockedOutUser()` | Banner displayed; Text = `"Epic sadface: Sorry, this user has been locked out."`. |
| `TC_LGN_003` | Empty Username Validation | **Automated** | `testEmptyCredentialsValidation()` (Excel) | Banner displayed; Text = `"Epic sadface: Username is required"`. |
| `TC_LGN_004` | Empty Password Validation | **Automated** | `testEmptyCredentialsValidation()` (Excel) | Banner displayed; Text = `"Epic sadface: Password is required"`. |
| `TC_LGN_005` | Invalid Credentials Rejection | **Automated** | `testInvalidCredentials()` (Excel) | Banner displayed; Text = `"Epic sadface: Username and password do not match..."`. |
| `TC_LGN_008` | Password Field Input Masking | **Automated** | `testPasswordInputMasking()` | `#password` element has attribute `type="password"`. |
| `TC_LGN_010` | Performance Glitch User Latency | **Automated** | `testPerformanceGlitchUserLogin()` | Dynamic `WaitUtils` absorbs ~5s delay; Lands on `/inventory.html`. |
| `TC_LGN_011` | Problem User Authentication | **Automated** | `testProblemUserLogin()` | Authenticates successfully; Lands on `/inventory.html`. |
| `TC_LGN_015` | Error Banner Dismissal | **Automated** | `testDismissErrorMessage()` | Click close 'X'; `isErrorMessageDisplayed() == false`. |
| `TC_LGN_018` | Route Guard on Inventory Access | **Automated** | `testRouteGuardDirectAccessInventory()` | Direct unauthenticated GET to `/inventory.html` redirects to `/` with error. |
| `TC_LGN_006-007, 009, 012-014, 016, 017, 019-022` | Edge cases (whitespace, CapsLock, mobile viewport, paste) | **Manual** | Manual Excel verification | Documented in `SauceDemo_Manual_TestCases.xlsx`. |

---

## 3. Module 2: Catalog & 4-Way Sorting (`InventoryTest.java`)
**Source Specification:** `TS-SD-CAT-001` (18 Test Cases)  
**Data Source:** Dynamic DOM Scraping (`InventoryPage.java`)

| Test ID | Test Scenario Title | Feasibility | Automation Method / Strategy | Assertion Criteria |
|:---|:---|:---:|:---|:---|
| `TC_CAT_001` | Catalog Grid 6-Item Rendering | **Automated** | `testCatalogRendering()` | Exactly 6 items; Titles non-empty; Prices > $0.00; Add-to-cart buttons active. |
| `TC_CAT_002` | Alphabetical Sort: Name (A to Z) | **Automated** | `testSortNameAtoZ()` | First item = "Sauce Labs Backpack"; Last item = "Test.allTheThings() T-Shirt (Red)". |
| `TC_CAT_003` | Reverse Sort: Name (Z to A) | **Automated** | `testSortNameZtoA()` | First item = "Test.allTheThings()..."; Last item = "Sauce Labs Backpack". |
| `TC_CAT_004` | Numerical Price Sort: Low to High | **Automated** | `testSortPriceLowToHigh()` | Parse prices as `Double`; Assert `Collections.sort()` matches; Min = $7.99, Max = $49.99. |
| `TC_CAT_005` | Numerical Price Sort: High to Low | **Automated** | `testSortPriceHighToLow()` | Assert reverse numeric order; Max = $49.99, Min = $7.99. |
| `TC_CAT_006` | Tied-Price Deterministic Grouping | **Automated** | `testTiedPriceSorting()` | Both $15.99 items (`Bolt T-Shirt` & `Red T-Shirt`) remain adjacent in list. |
| `TC_CAT_007-018` | Thumbnail clicks, page refresh, mobile layout, whitespace dead clicks | **Manual** | Manual Excel verification | Documented in `SauceDemo_Manual_TestCases.xlsx`. |

---

## 4. Module 3: Product Details (`ProductDetailsTest.java`)
**Source Specification:** `TS-SD-DTL-001` (18 Test Cases)  
**Data Source:** Verbatim Catalog Cross-Matching

| Test ID | Test Scenario Title | Feasibility | Automation Method / Strategy | Assertion Criteria |
|:---|:---|:---:|:---|:---|
| `TC_DTL_001` | Product Details Drilldown Navigation | **Automated** | `testProductDetailsNavigationAndDataIntegrity()` | Click title link; URL contains `inventory-item.html?id=4`. |
| `TC_DTL_003` | Verbatim Product Data Integrity | **Automated** | `testProductDetailsNavigationAndDataIntegrity()` | Title = "Sauce Labs Backpack"; Price = "$29.99"; Description matches catalog. |
| `TC_DTL_004` | Hero Image Asset Rendering | **Automated** | `testProductDetailsNavigationAndDataIntegrity()` | JavaScript execution: `naturalWidth > 0 && naturalHeight > 0`. |
| `TC_DTL_005` | Add to Cart from Details Page | **Automated** | `testDetailsAddToCartAndCatalogStateSync()` | Button toggles to 'Remove'; Header badge increments to '1'. |
| `TC_DTL_007` | Return Navigation via 'Back to products' | **Automated** | `testBackToProductsNavigation()` | Click `#back-to-products`; URL = `/inventory.html`; 6 items displayed. |
| `TC_DTL_008` | Bidirectional State Sync: Details $\rightarrow$ Catalog | **Automated** | `testDetailsAddToCartAndCatalogStateSync()` | Add item on details; Return to catalog; Card button displays red 'Remove'. |
| `TC_DTL_002, 006, 009-018` | Query param bounds (`?id=9999`, `?id=invalid`), social links, native back | **Manual** | Manual Excel verification | Documented in `SauceDemo_Manual_TestCases.xlsx`. |

---

## 5. Module 4: Shopping Cart & Badge Transitions (`ShoppingCartTest.java`)
**Source Specification:** `TS-SD-CRT-001` (18 Test Cases)  
**Data Source:** Multi-item Cart Actions (`CartPage.java`)

| Test ID | Test Scenario Title | Feasibility | Automation Method / Strategy | Assertion Criteria |
|:---|:---|:---:|:---|:---|
| `TC_CRT_001` | Add Single Item from Inventory | **Automated** | `testAddSingleProductToCart()` | Button flips to 'Remove'; Cart badge increments to '1'. |
| `TC_CRT_003` | Add Multiple Products to Cart | **Automated** | `testAddMultipleProductsToCart()` | Add Backpack, Bike Light, Bolt T-Shirt; Badge increments to '3'. |
| `TC_CRT_004` | Sequential Badge Count Progression | **Automated** | `testSequentialBadgeIncrement()` | Verify sequential transitions: $0 \rightarrow 1 \rightarrow 2 \rightarrow 3$. |
| `TC_CRT_005` | Remove Product Inside Cart View | **Automated** | `testRemoveItemInsideCart()` | Open `/cart.html`; Click 'Remove'; Row removed; Badge decrements $2 \rightarrow 1$. |
| `TC_CRT_006` | Remove Product from Inventory View | **Automated** | `testRemoveItemDirectlyFromInventory()` | Click 'Remove' on catalog card; Button resets to 'Add to cart'; Badge deleted. |
| `TC_CRT_009` | Zero-Badge DOM Element Deletion | **Automated** | `testZeroBadgeDOMDeletion()` | Remove last item; Assert `.shopping_cart_badge` deleted from DOM (does NOT show '0'). |
| `TC_CRT_010` | Empty Cart Table Layout | **Automated** | `testEmptyCartLayout()` | On `/cart.html`, table displays 0 `.cart_item` rows; Buttons present. |
| `TC_CRT_011` | Cart Contents Verbatim Attribute Match | **Automated** | `testCartItemVerbatimIntegrity()` | Title, description, and price on `/cart.html` verbatim match inventory card. |
| `TC_CRT_016` | Application State Reset via Menu | **Automated** | `testResetAppStatePurgesCart()` | Trigger 'Reset App State' in menu; All buttons reset to 'Add to cart'; Badge purged. |
| `TC_CRT_002, 007, 008, 012-015, 017-018` | Rapid state flapping, max 6 items, empty cart checkout | **Manual** | Manual Excel verification | Documented in `SauceDemo_Manual_TestCases.xlsx`. |

---

## 6. Module 5: End-to-End Checkout Flow (`CheckoutE2ETest.java`)
**Source Specification:** `TS-SD-CHK-001` (20 Test Cases)  
**Data Source:** Math Calculation Algorithms + `models/CheckoutCustomer.java`

| Test ID | Test Scenario Title | Feasibility | Automation Method / Strategy | Assertion Criteria |
|:---|:---|:---:|:---|:---|
| `TC_CHK_001` | Checkout Initiation from Cart | **Automated** | `testEndToEndCheckout()` | Click `#checkout`; Lands on `/checkout-step-one.html`. |
| `TC_CHK_002` | Valid Customer Info Progression | **Automated** | `testEndToEndCheckout()` | Enter Name/Zip; Click 'Continue'; Lands on `/checkout-step-two.html`. |
| `TC_CHK_003` | Missing First Name Validation | **Automated** | `testMissingFirstNameValidation()` | Leave First Name empty; Error = `"Error: First Name is required"`. |
| `TC_CHK_004` | Missing Last Name Validation | **Automated** | `testMissingLastNameValidation()` | Leave Last Name empty; Error = `"Error: Last Name is required"`. |
| `TC_CHK_005` | Missing Postal Code Validation | **Automated** | `testMissingPostalCodeValidation()` | Leave Postal empty; Error = `"Error: Postal Code is required"`. |
| `TC_CHK_007` | All Required Fields Empty Priority | **Automated** | `testAllFieldsEmptyValidationPriority()` | Leave all empty; Validates priority: `"Error: First Name is required"`. |
| `TC_CHK_009` | Multi-Item Order Summary Listing | **Automated** | `testOrderSummaryItemEnumeration()` | Cart items on Step Two match added products verbatim. |
| `TC_CHK_010` | Item Subtotal Mathematical Addition | **Automated** | `testMathematicalTotalAccuracy()` | Extract line item: `Item total: $XX.XX` == Exact sum of product prices. |
| `TC_CHK_011` | 8% Tax Calculation & Rounding | **Automated** | `testMathematicalTotalAccuracy()` | Line item `Tax: $X.XX` strictly computes at $\text{round}(\text{Subtotal} \times 0.08, 2)$. |
| `TC_CHK_012` | Total Formula: Subtotal + Tax | **Automated** | `testMathematicalTotalAccuracy()` | Strict equality: $\text{Subtotal} + \text{Tax} == \text{Total}$ down to the exact cent. |
| `TC_CHK_013` | Order Completion Header Rendering | **Automated** | `testEndToEndCheckout()` | Click 'Finish'; Lands on `/checkout-complete.html`; Header = `"Thank you for your order!"`. |
| `TC_CHK_014` | Client Cart Purged Post-Completion | **Automated** | `testEndToEndCheckout()` | Post-order cart badge deleted from DOM; `/cart.html` has 0 items. |
| `TC_CHK_006, 008, 015-020` | Cancel button routing, empty cart checkout boundary, inline error dismiss | **Manual** | Manual Excel verification | Documented in `SauceDemo_Manual_TestCases.xlsx`. |

---

## 7. Module 6: Logout, Navigation & Cart Persistence (`SessionLogoutTest.java`)
**Source Specification:** `TS-SD-SESS-001` (20 Test Cases)  
**Data Source:** State Transition & Storage Verification (`MenuComponent.java`)

| Test ID | Test Scenario Title | Feasibility | Automation Method / Strategy | Assertion Criteria |
|:---|:---|:---:|:---|:---|
| `TC_SES_001` | Successful Logout via Hamburger Menu | **Automated** | `testLogoutFlow()` | Click menu $\rightarrow$ Logout; URL redirects to `https://www.saucedemo.com/`. |
| `TC_SES_002` | Post-Logout Form State & Cookie Token Destruction | **Automated** | `testSessionTokenDestruction()` | Post-logout, cookie `session-username` is `null`/deleted; Inputs empty. |
| `TC_SES_003` | Protected Route Access Post-Logout: Inventory | **Automated** | `testDirectAccessGuardPostLogout()` | Direct GET to `/inventory.html` blocked; Red banner = `"Epic sadface: You can only access '/inventory.html'..."`. |
| `TC_SES_004` | Protected Route Access Post-Logout: Cart | **Automated** | `testDirectAccessGuardPostLogout()` | Direct GET to `/cart.html` blocked; Redirected with unauthorized error. |
| `TC_SES_006` | Native Browser 'Back' Button Invalidation | **Automated** | `testBackButtonSessionDenial()` | Logout; Click browser Back; Session remains terminated on login page. |
| `TC_SES_007` | Re-Authentication with Same Credentials | **Automated** | `testReAuthenticationLifecycle()` | Re-login as `standard_user`; Recreates cookie; Redirects to `/inventory.html`. |
| `TC_SES_008` | Single-Product Cart Persistence Across Re-Login | **Automated** | `testCartPersistenceAcrossReLogin()` | Add Backpack (badge=1); Logout; Re-login; Header badge = '1'; Backpack in `/cart.html`. |
| `TC_SES_009` | Multi-Product Cart Persistence Across Re-Login | **Automated** | `testMultiItemCartPersistenceAcrossReLogin()` | Add 3 items; Logout; Re-login; Badge = '3'; All 3 items intact in `/cart.html`. |
| `TC_SES_015` | Action Button Re-Hydration Post-Re-Login | **Automated** | `testButtonStateReHydrationPostReLogin()` | Add Backpack; Logout; Re-login; Backpack button re-hydrates dynamically as red 'Remove'. |
| `TC_SES_005, 010-014, 016-020` | Drawer animation timing, tab teardown, cross-user isolation, rapid hops | **Manual** | Manual Excel verification | Documented in `SauceDemo_Manual_TestCases.xlsx`. |