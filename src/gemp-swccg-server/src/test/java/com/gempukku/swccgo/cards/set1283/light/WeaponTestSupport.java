package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;

/**
 * Shared steps for the Virtual Block 8.3 Light weapon tests: Light Side initiates a battle and fires a weapon at a
 * target with prepared weapon destiny.
 */
final class WeaponTestSupport {
    private WeaponTestSupport() {
    }

    static void lsBattleAndFire(VirtualTableScenario scn, PhysicalCardImpl site, PhysicalCardImpl weapon, PhysicalCardImpl target, int... destinies) {
        scn.EnsureLSForcePile(4);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        for (int i = destinies.length - 1; i >= 0; i--) {
            scn.PrepareLSDestiny(destinies[i]);
        }
        scn.LSUseCardAction(weapon, "Fire");
        if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(target)) {
            scn.LSChooseCard(target);
        }
        scn.PassForceUseResponses();
        scn.PassWeaponFireWithDestinyDraw(destinies.length);
        scn.PassAllResponses();
    }
}
