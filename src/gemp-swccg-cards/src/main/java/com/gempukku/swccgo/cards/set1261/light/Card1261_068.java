package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.ConvertCharacterEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Lando Calrissian, Scoundrel (V), Virtual Block 6.1. */
public class Card1261_068 extends AbstractRebel {
    public Card1261_068() {
        super(Side.LIGHT, 1, 4, 4, 3, 7, "Lando Calrissian, Scoundrel", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("With a silvery tongue or a rapid-fire blaster, Lando prides himself on being able to get out of tight situations. Is at his best when cornered.");
        setGameText("Adds 2 to power of anything he pilots. Once per game, may convert opponent's Lando present at a site (may retrieve 2 Force). Adds one battle destiny if with your alien or Rebel. Immune to attrition < 3 (< 5 if your Effect with 'Lando' in lore on table).");
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addPersona(Persona.LANDO);
        setMatchingStarshipFilter(Filters.Falcon);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        return Arrays.asList(new AddsPowerToPilotedBySelfModifier(self, 2),
                new AddsBattleDestinyModifier(self, new WithCondition(self,
                        Filters.and(Filters.your(playerId), Filters.or(Filters.alien, Filters.Rebel))), 1),
                new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(3, 5,
                        new OnTableCondition(self, Filters.and(Filters.your(playerId), Filters.Effect, Filters.loreContains("Lando"))))));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId actionId = GameTextActionId.LEGACY__LANDO_SCOUNDREL__CONVERT;
        Filter target = Filters.and(Filters.opponents(self), Filters.Lando, Filters.presentAt(Filters.site));
        if (GameConditions.isOncePerGame(game, self, actionId) && GameConditions.canTarget(game, self, target)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, actionId);
            action.setText("Convert opponent's Lando");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose opponent's Lando present", target) {
                @Override
                protected void cardTargeted(int targetGroupId, PhysicalCard targetedCard) {
                    action.addAnimationGroup(targetedCard);
                    action.allowResponses("Convert " + GameUtils.getCardLink(targetedCard), new UnrespondableEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new ConvertCharacterEffect(action, action.getPrimaryTargetCard(targetGroupId)));
                            action.appendEffect(new PlayoutDecisionEffect(action, playerId, new YesNoDecision("Do you want to retrieve 2 Force?") {
                                @Override
                                protected void yes() { action.appendEffect(new RetrieveForceEffect(action, playerId, 2)); }
                            }));
                        }
                    });
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
