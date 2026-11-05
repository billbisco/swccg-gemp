# Wave handoff: Virtual Block 7.2 Light Effects / Interrupt / location (branch legacy/p-vb7-2-light-b)

Stopped early on coordinator request. Java for all 8 cards compiles (mvn install -DskipTests OK). NO tests written yet; legacy_check not run; full suite not run.

## Cards (Java written, untested)
- 1272_20 Declaration Of Rebellion: Bail/Leia/Mon Mothma get Keyword.INSURGENT; total battle destiny +1 (global, conditioned on your Insurgent present at a battleground); hit Insurgent at a battleground with your non-unique character forfeits for 4 (ForfeitValueToUse, only when its forfeit < 4); once per game take K'lor'slug. Destiny 5 from the card image (pack says 4).
  Center Of Tyranny hook: `ModifyGameTextType.LEGACY__DECLARATION_OF_REBELLION__ONE_IS_TWO`; Center Of Tyranny should add `new ModifyGameTextModifier(self, Filters.title("Declaration Of Rebellion"), ModifyGameTextType.LEGACY__DECLARATION_OF_REBELLION__ONE_IS_TWO)` while A Liberated World is up. Then the +1 becomes +2.
- 1272_22 Rebel Gunrunner: printed text. Stacks a weapon from Reserve when deployed; deploys it on a participating card when a battle is initiated; returns it at end of weapons segment / end of battle / when about to leave table (WhileInPlayData tracking).
- 1272_23 Rogue Insertion: PRINTED sheet text (spies; unless your ability > 4 character at a site: power +2, armor = 4, immune to attrition < 4, weapon destiny targeting them -1, may not be further reduced). The card image shows the later rev1 text instead.
- 1272_24 Rogue Squadron Tactics, 1272_25 I Hope She's All Right (V), 1272_26 The Camp (V), 1272_27 Clinging To The Edge (V), 1272_28 Roche (V; Light 2 / Dark 1 Force icons from the image, Dark text kept from 7_124).

## Engine and PC-file edits
- New: `logic/modifiers/LegacyWeaponDestinyMayNotBeFurtherReducedModifier.java`, `cards/LegacyInsurgentFilters.java` (Insurgent = Keyword.INSURGENT or "Insurgent" in lore; the 7.2 characters wave may want to reuse it).
- One-line hook in `querying/Destiny.java` getWeaponDestiny (applyLimit). One `ModifierType` line, one `ModifyGameTextType` line, 8 `GameTextActionId` LEGACY__ lines (before LEGACY__TARFFUL__ROAR).

## Gaps and interpretations
- Icons: the pack lists none for 22, 23, 24 and 25, and none were added. The card images show expansion icons (25 is probably Endor, as on base 8_038), so check them.
- I Hope She's All Right (V) "neither may be increased above 6": defense uses MaximumDefenseValueModifiedTo 6; forfeit uses ForfeitIncreaseLimit (6 - printed).
- The "may not be further reduced" floor is (drawn value - 1). It ignores positive modifiers.
- To do: the tests (one file per card), mutation checks, legacy_check.py, blueprintMapping/CardImages lines if needed, then the full suite.
