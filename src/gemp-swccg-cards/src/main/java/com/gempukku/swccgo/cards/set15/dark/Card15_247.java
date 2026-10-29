package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.common.*;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import java.util.Collections;
import java.util.List;

public class Card15_247 extends AbstractLostInterrupt {
    public Card15_247() {
        super(Side.DARK, 4, "I Have You Now & Your Powers Are Weak, Old Man", Uniqueness.UNRESTRICTED, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("");
        setGameText("If a Dark Jedi and a Rebel with ability > 2 are involved in the same battle, you may add one battle destiny (add two destiny if Rebel is Luke, three destiny if Rebel is Obi-Wan).");
        addComboCardTitles("I Have You Now", "Your Powers Are Weak, Old Man");
        addIcons(Icon.REFLECTIONS_GOLD);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        Filter jedi = Filters.and(Filters.Dark_Jedi, Filters.participatingInBattle);
        Filter rebel = Filters.and(Filters.Rebel, Filters.abilityMoreThan(2), Filters.participatingInBattle);
        if (!GameConditions.canAddBattleDestinyDraws(game, self)
                || !GameConditions.canTarget(game, self, jedi) || !GameConditions.canTarget(game, self, rebel)) return null;
        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Add battle destiny");
        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose Dark Jedi", jedi) {
            @Override
            protected void cardTargeted(int jediGroup, PhysicalCard darkJedi) {
                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose Rebel", rebel) {
                    @Override
                    protected void cardTargeted(int rebelGroup, PhysicalCard target) {
                        action.addAnimationGroup(darkJedi, target);
                        action.allowResponses(new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                PhysicalCard finalRebel = targetingAction.getPrimaryTargetCard(rebelGroup);
                                int bonus = Filters.ObiWan.accepts(game, finalRebel) ? 3
                                        : Filters.Luke.accepts(game, finalRebel) ? 2 : 1;
                                action.appendEffect(new AddBattleDestinyEffect(action, bonus));
                            }
                        });
                    }
                });
            }
        });
        return Collections.singletonList(action);
    }
}
