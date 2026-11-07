package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.List;

/** Vader's Mask, Virtual Block 7.1. */
public class Card1271_017 extends LegacyVaderEquipment {
    public Card1271_017() {
        super(0, "Vader's Mask");
        setLore("Darth Vader's armored mask and life-support system provide him with extraordinary physical protection in duels and battles, in addition to his Dark Jedi combat skill.");
        setGameText("Deploy on [Vader] Vader. During your battle phase, may cancel this Effect to allow Vader to make a regular move. In order for opponent's character to move away from here using landspeed, opponent must first lose 1 Force. While defending a battle at opponent's site, Vader is power +2 and immune to attrition. (Immune to Alter.)");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter wearer = Filters.hasAttached(self);
        String opponent = game.getOpponent(self.getOwner());
        Condition defendingAtOpponentsSite = (state, modifiers) -> GameConditions.isDuringBattleInitiatedBy(game, opponent)
            && GameConditions.isDuringBattleAt(game, Filters.and(Filters.site, Filters.owner(opponent)))
            && GameConditions.isDuringBattleWithParticipant(game, wearer);
        return List.of(new PowerModifier(self, wearer, defendingAtOpponentsSite, 2),
            new ImmuneToAttritionModifier(self, wearer, defendingAtOpponentsSite));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        PhysicalCard vader = self.getAttachedTo();
        if (vader == null || !GameConditions.isDuringYourPhase(game, playerId, Phase.BATTLE)
                || !GameConditions.canTargetToCancel(game, self, self)
                || !Filters.movableAsRegularMove(playerId, false, 0, false, Filters.any).accepts(game, vader)) return null;

        TopLevelGameTextAction action = new TopLevelGameTextAction(self, sourceId);
        action.setText("Cancel Vader's Mask to move Vader");
        action.appendCost(new CancelCardOnTableEffect(action, self));
        action.appendEffect(new MoveCardAsRegularMoveEffect(action, playerId, vader, false, false, Filters.any));
        return List.of(action);
    }
}
