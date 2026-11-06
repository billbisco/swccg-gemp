package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostAboardModifier;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Legacy Block 6.1 Admiral Piett. */
public class Card1261_002 extends AbstractImperial {
    public Card1261_002() {
        super(Side.DARK, 1, 4, 3, 3, 6, "Admiral Piett", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        addPersona(Persona.PIETT);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.DEATH_STAR_II);
        addKeywords(Keyword.ADMIRAL, Keyword.LEADER);
        setMatchingStarshipFilter(Filters.Executor);
        setLore("Veteran of the Imperial military machine. Leader of the Imperial fleet at Endor. Skilled at political maneuvering and appeasing his powerful superiors.");
        setGameText("Adds 3 to power of anything he pilots. Deploys -2 to Executor (while aboard, it is immune to attrition < 5 and hyperspeed +1). During battle, if Flagship Operations on table, may place a card from hand in Used Pile; your total power is +3.");
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new DeployCostAboardModifier(self, -2, Persona.EXECUTOR));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter executorAboard = Filters.and(Filters.Executor, Filters.hasAboard(self));
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, executorAboard, 5));
        modifiers.add(new HyperspeedModifier(self, executorAboard, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.canSpot(game, self, Filters.Flagship_Operations)
                && GameConditions.hasHand(game, playerId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Add 3 to total power");
            action.appendUsage(new OncePerBattleEffect(action));
            action.appendCost(new PutCardFromHandOnUsedPileEffect(action, playerId));
            action.appendEffect(new ModifyTotalPowerUntilEndOfBattleEffect(action, 3, playerId, "Adds 3 to total power"));
            return Collections.singletonList(action);
        }
        return null;
    }
}
