package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/** A character that must deploy undercover at specific sites, and deploys normally elsewhere. */
public interface ConditionalUndercoverSpyDeployment {
    boolean mustDeployUndercoverAt(SwccgGame game, PhysicalCard self, PhysicalCard target);

    static boolean isRequiredAt(SwccgGame game, PhysicalCard character, PhysicalCard target) {
        return character.getBlueprint() instanceof ConditionalUndercoverSpyDeployment
                && ((ConditionalUndercoverSpyDeployment) character.getBlueprint()).mustDeployUndercoverAt(game, character, target);
    }
}
