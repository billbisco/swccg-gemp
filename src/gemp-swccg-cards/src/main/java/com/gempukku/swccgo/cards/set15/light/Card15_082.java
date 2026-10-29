package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Card15_082 extends AbstractLostInterrupt {
    public Card15_082() {
        super(Side.LIGHT, 5, "Gift Of The Mentor & Skywalkers", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("");
        setGameText("If Luke is with Obi-Wan or Leia in a battle, add two battle destiny. (Four if with both.)");
        addComboCardTitles("Gift Of The Mentor", "Skywalkers");
        addIcons(Icon.REFLECTIONS_GOLD);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        Filter luke = Filters.and(Filters.Luke, Filters.participatingInBattle);
        Filter obiWan = Filters.and(Filters.ObiWan, Filters.participatingInBattle, Filters.with(self, luke));
        Filter leia = Filters.and(Filters.Leia, Filters.participatingInBattle, Filters.with(self, luke));
        boolean withObiWan = GameConditions.canSpot(game, self, obiWan);
        boolean withLeia = GameConditions.canSpot(game, self, leia);
        if (!GameConditions.canTarget(game, self, luke) || (!withObiWan && !withLeia)
                || !GameConditions.canAddBattleDestinyDraws(game, self)) {
            return null;
        }
        if ((withObiWan && !GameConditions.canTarget(game, self, obiWan))
                || (withLeia && !GameConditions.canTarget(game, self, leia))) {
            return null;
        }
        int bonus = withObiWan && withLeia ? 4 : 2;
        int targets = withObiWan && withLeia ? 3 : 2;
        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Add " + bonus + " battle destiny");
        action.appendTargeting(new TargetCardsOnTableEffect(action, playerId, "Choose Luke and companions", targets, targets,
                Filters.or(luke, obiWan, leia)) {
            @Override
            protected boolean getUseShortcut() { return true; }
            @Override
            protected void cardsTargeted(int targetGroupId, Collection<PhysicalCard> cards) {
                action.addAnimationGroup(cards);
                action.allowResponses(new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(new AddBattleDestinyEffect(action, bonus));
                    }
                });
            }
        });
        return Collections.singletonList(action);
    }
}
