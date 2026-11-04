# Wave handoff: Virtual Block 7.1 Dark (a), branch legacy/p-vb7-1-dark-a

Status: research only. No card Java or tests written yet. Stopped at the coordinator's handoff request.

Cards (none done): 1271_1 Cyborg Commander, Hunter Of Jedi; 1271_2 Darth Vader, More Machine Than Man;
1271_7 Sergeant Torent (V); 1271_8 A Sith's Weapon; 1271_10 I Want Every Part Of This Ship Checked;
1271_11 I'll Take Them Myself; 1271_12 Imperial Security Bureau; 1271_13 Vader's Bionic Limbs;
1271_14 Vader's Cape (V); 1271_15 Vader's Cybernetic Enhancements.

Tests run: none. CardImages.js already has all ten ids; no blueprintMapping lines are needed (no 601 aliases).

## Printed text (from the sheet and card images; use it in place of the pack `front`)
- 1271_1: title "Cyborg Commander, Hunter Of Jedi" (persona GRIEVOUS; 1274_11 matches it). "[Virtual Block 5] Dark Jedi
  Lightsaber may deploy on Cyborg Commander. ... your battle destiny draws here are +1 ..." Printed lore: "Leader. "He's no Jedi."" (keyword LEADER).
  Force-sensitive is printed, but there's no representation for it in GEMP.
- 1271_2: "[Pilot] 3. [Vader] cards never deploy unless Rise Of The Dark Lord on table. Once per turn, if at a site you control,
  may apply his ability to draw battle destiny elsewhere. Immune to Clash Of Sabers, Disarmed, and attrition < 5."
- Sheet icons: down-arrow = deploy from Reserve Deck; reshuffle. Up-arrow = take into hand from Reserve Deck; reshuffle.
  The blank in 1271_10 is [Grabber]. The blanks in 1271_11 and 1271_12 are [Setup]. A trailing "A" means Immune to Alter.

## Engine findings / plan
- There's no Vader icon. Add `Icon.VADER("Vader")` next to SIDIOUS/SKYWALKER (one line; Icon.java is a one-liner file).
  1271_16, 1271_17 and 1271_2 need it too.
- 1271_2 "apply ability elsewhere": follow Card1240_053 (ApplyAbilityToDrawBattleDestinyEffect, once per turn).
- 1271_1 lightsaber grant: MayDeployToTargetModifier + MayUseWeaponModifier (as in Card225_045), with filter title "Dark Jedi Lightsaber" + Icon.LEGACY_BLOCK_5 (601_154).
- 1271_8 "swing again": optional after trigger on FIRED_WEAPON (TriggerConditions.weaponJustFiredBy). The target is still not hit.
  Cost: lose 1 Force. Then push weapon.getBlueprint().getFireWeaponAction(..., fireAtTargetFilter = same target, ignorePerAttackOrBattleLimit = true).
- 1271_12 "Interrupts ... may be played as USED: ...": there's no generic hook. Needs a new Legacy helper plus a one-line hook in
  AbstractInterrupt.getGrantedTopLevelActions (a PC file).
- 1271_10 "card was not found -> search Force Pile instead": there's no failed-search trigger in GEMP. Needs new engine work,
  the largest gap in this wave.
- 1271_13 "about to be lost before damage segment -> instead 'hit'": see the 'instead hit' precedents Card200_128 and Card1291_072.
- 1271_15 "move as a 'react'": see Card1281_008 and Card7_217.
