package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardAboardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Collections;
import java.util.List;

/** Legacy Block 6.1 Blizzard 4. */
public class Card1261_058 extends AbstractCombatVehicle {
    public Card1261_058() {
        super(Side.DARK, 2, 6, 6, 7, null, 1, 6, "Blizzard 4", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        addModelType(ModelType.AT_AT);
        addIcons(Icon.PILOT, Icon.SCOMP_LINK, Icon.REFLECTIONS_III);
        addKeywords(Keyword.ENCLOSED);
        setPilotCapacity(1);
        setPassengerCapacity(6);
        setLore("Staffed by an experienced crew. Blizzard 4's role in all engagements is to seek out new terrain uncovered by the initial offense thrust and unload troops to hold it. Enclosed.");
        setGameText("May add 1 pilot and 6 passengers. Permanent pilot provides ability of 2. When deployed, may deploy a non-pilot Imperial warrior aboard (for free). Immune to attrition < 4.");
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new ImmuneToAttritionLessThanModifier(self, 4));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggersEvenIfUnpiloted(String playerId, SwccgGame game,
            EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId id = GameTextActionId.BLIZZARD_4__DOWNLOAD_IMPERIAL_WARRIOR;
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, id);
            action.setText("Deploy a non-pilot Imperial warrior from Reserve Deck");
            action.appendEffect(new DeployCardAboardFromReserveDeckEffect(action,
                    Filters.and(Filters.Imperial, Filters.warrior, Filters.not(Filters.pilot)), Filters.sameCardId(self), true, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
