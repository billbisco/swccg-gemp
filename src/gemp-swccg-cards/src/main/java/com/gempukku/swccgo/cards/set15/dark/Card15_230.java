package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.common.*;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import java.util.Collections;
import java.util.List;

public class Card15_230 extends AbstractLostInterrupt {
    public Card15_230() {
        super(Side.DARK, 3, "Dark Collaboration & Charming To The Last", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_GOLD, Rarity.UNKNOWN);
        setLore("");
        setGameText("If Tarkin and either Tagge or Motti are in a battle together, you may add two battle destiny. (If Tarkin is also with Leia, add two additional battle destiny.)");
        setTitleComponents(new TitleComponent("Dark Collaboration",Uniqueness.UNIQUE),new TitleComponent("Charming To The Last",Uniqueness.UNRESTRICTED));
        addIcons(Icon.REFLECTIONS_GOLD);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        Filter companion = Filters.and(Filters.or(Filters.Tagge, Filters.Motti), Filters.participatingInBattle, Filters.canBeTargetedBy(self));
        Filter targetableLeiaIfPresent = new Filter() {
            @Override
            public boolean accepts(GameState state, ModifiersQuerying modifiers, PhysicalCard candidate) {
                Filter leia = Filters.and(Filters.Leia, Filters.with(self, Filters.sameCardId(candidate)));
                return !GameConditions.canSpot(game, self, leia) || GameConditions.canTarget(game, self, leia);
            }
        };
        Filter tarkin = Filters.and(Filters.Tarkin, Filters.participatingInBattle, Filters.with(self, companion), targetableLeiaIfPresent);
        if (!GameConditions.canAddBattleDestinyDraws(game, self)
                || !GameConditions.canTarget(game, self, tarkin) || !GameConditions.canTarget(game, self, companion)) return null;
        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Add battle destiny");
        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose Tarkin", tarkin) {
            @Override
            protected void cardTargeted(int tarkinGroup, PhysicalCard targetTarkin) {
                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose Tagge or Motti",
                        Filters.and(companion, Filters.with(self, Filters.sameCardId(targetTarkin)))) {
                    @Override
                    protected void cardTargeted(int companionGroup, PhysicalCard officer) {
                        action.addAnimationGroup(targetTarkin, officer);
                        Filter leia = Filters.and(Filters.Leia, Filters.with(self, Filters.sameCardId(targetTarkin)));
                        if (GameConditions.canSpot(game, self, leia)) {
                            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose Leia", leia) {
                                @Override
                                protected void cardTargeted(int leiaGroup, PhysicalCard targetLeia) {
                                    action.addAnimationGroup(targetLeia);
                                    addDestiny(action, 4);
                                }
                            });
                        } else addDestiny(action, 2);
                    }
                });
            }
        });
        return Collections.singletonList(action);
    }

    private void addDestiny(PlayInterruptAction action, int count) {
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                action.appendEffect(new AddBattleDestinyEffect(action, count));
            }
        });
    }
}
