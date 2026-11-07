package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPreventMovingAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.List;

/** Dressel, Legacy Block 6.3, final Light master page 39. */
public class Card1263_030 extends AbstractSystem {
    private static final GameTextActionId RETRIEVE = GameTextActionId.LEGACY__DRESSEL__RETRIEVE_TROOPER;

    public Card1263_030() {
        super(Side.LIGHT, "Dressel", 2, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setLocationLightSideGameText("During your control phase, if you occupy, may lose 1 Force to retrieve a trooper into hand.");
        setLocationDarkSideGameText("Force drain -1 here. Despair may not prevent cards from moving as a 'react' (except away from battle).");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.PLANET);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        if (!GameConditions.isDuringYourPhase(game, playerId, Phase.CONTROL)
                || !GameConditions.occupies(game, playerId, self)
                || !GameConditions.canSearchLostPile(game, playerId, self, RETRIEVE)) {
            return null;
        }
        TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, RETRIEVE);
        action.setText("Lose 1 Force to retrieve a trooper into hand");
        action.appendCost(new LoseForceEffect(action, playerId, 1, true));
        action.appendEffect(new RetrieveCardIntoHandEffect(action, playerId, Filters.trooper));
        return List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerId, SwccgGame game, PhysicalCard self) {
        return List.of(new ForceDrainModifier(self, self, -1, playerId),
                new MayNotPreventMovingAsReactModifier(self, Filters.Despair));
    }
}
