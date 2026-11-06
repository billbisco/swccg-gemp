package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyAndChooseInsteadEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/** Dash In Rogue 10, Virtual Block 6.1. */
public class Card1261_109 extends AbstractCombatVehicle {
    public Card1261_109() {
        super(Side.LIGHT, 2, 3, 5, null, 5, 4, 5, "Dash In Rogue 10", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("Enclosed.");
        setGameText("May add 1 pilot. Permanent pilot is •Dash, who provides ability of 3. During battle, if with another T-47 and about to draw battle destiny, may draw two and choose one. Immune to attrition < 4.");
        addModelType(ModelType.T_47);
        addIcons(Icon.HOTH, Icon.PILOT);
        addKeywords(Keyword.ENCLOSED, Keyword.SNOWSPEEDER, Keyword.ROGUE_SQUADRON);
        setPilotCapacity(1);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(Persona.DASH, 3) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new ImmuneToAttritionLessThanModifier(self, 4));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isAboutToDrawBattleDestiny(game, effectResult, playerId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canSpot(game, self, Filters.and(Filters.other(self), Filters.T_47, Filters.with(self)))
                && GameConditions.canDrawDestinyAndChoose(game, 1)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw two battle destiny and choose one");
            action.appendEffect(new DrawDestinyAndChooseInsteadEffect(action, 2, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
