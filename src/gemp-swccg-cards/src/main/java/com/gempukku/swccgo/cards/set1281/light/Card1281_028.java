package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayUseWeaponModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Subtype: Rebel
 * Title: Kyle Katarn
 */
public class Card1281_028 extends AbstractRebel {
    public Card1281_028() {
        super(Side.LIGHT, 1, 5, 4, 5, 8, "Kyle Katarn", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setLore("Scoundrel");
        setGameText("May use any stolen weapon. Opponent may not react to or from same site. Your total battle destiny +1 here, for each (Scoundrel) Effect on table. Once per game, may make a regular move during your control phase. Immune to attrition < 4 (< 5 if armed).");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.SCOUNDREL);
        addPersona(Persona.KYLE_KATARN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter here = Filters.here(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayUseWeaponModifier(self, Filters.and(Filters.stolen, Filters.weapon)));
        modifiers.add(new MayNotReactToLocationModifier(self, here, opponent));
        modifiers.add(new MayNotReactFromLocationModifier(self, here, opponent));
        modifiers.add(new TotalBattleDestinyModifier(self, here,
                new OnTableEvaluator(self, Filters.and(Icon.SCOUNDREL, Filters.Effect)), playerId, true));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(4, 5, new ArmedWithCondition(self, Filters.weapon))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__KYLE_KATARN__REGULAR_MOVE;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, playerId, Phase.CONTROL)
                && Filters.movableAsRegularMove(playerId, false, 0, false, Filters.any).accepts(game, self)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Make a regular move");
            action.setActionMsg("Have Kyle Katarn make a regular move");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new MoveCardAsRegularMoveEffect(action, playerId, self, false, false, Filters.any));
            return Collections.singletonList(action);
        }
        return null;
    }
}
