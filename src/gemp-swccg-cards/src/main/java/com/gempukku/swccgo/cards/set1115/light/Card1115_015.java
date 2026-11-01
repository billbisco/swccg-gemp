package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.ConvertCharacterEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Character
 * Subtype: Alien
 * Title: Lando Calrissian
 */
public class Card1115_015 extends AbstractAlien {
    public Card1115_015() {
        super(Side.LIGHT, 1, 3, 3, 3, 6, "Lando Calrissian", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("Former owner of the Millennium Falcon. Despite a notorious past, has become a successful chief administrator of a Tibanna gas mining facility. Scoundrel and Gambler.");
        setGameText("Adds 2 to power of anything he pilots (3 if piloting Falcon). May convert opponent's Lando present at a site. While with Leia, Lando is Heroic. During battle, may use 1 Force to cancel the game text of opponent's character of ability < 5 (or with a [Permanent Weapon]) at same site.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.CLOUD_CITY);
        addKeywords(Keyword.GAMBLER);
        addPersona(Persona.LANDO);
        setMatchingStarshipFilter(Filters.Falcon);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.Falcon)));
        modifiers.add(new KeywordModifier(self, new WithCondition(self, Filters.Leia), Keyword.HEROIC));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        Filter opponentsLando = Filters.and(Filters.opponents(self), Filters.Lando, Filters.presentAt(Filters.site));
        if (GameConditions.canTarget(game, self, opponentsLando)) {
            final TopLevelGameTextAction convertAction = new TopLevelGameTextAction(self, gameTextSourceCardId);
            convertAction.setText("Convert opponent's Lando");
            convertAction.appendTargeting(
                    new TargetCardOnTableEffect(convertAction, playerId, "Choose opponent's Lando to convert", opponentsLando) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            convertAction.addAnimationGroup(targetedCard);
                            convertAction.allowResponses("Convert " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(convertAction) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = convertAction.getPrimaryTargetCard(targetGroupId);
                                            convertAction.appendEffect(
                                                    new ConvertCharacterEffect(convertAction, finalTarget));
                                        }
                                    });
                        }
                    });
            actions.add(convertAction);
        }

        Filter cancelFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.atSameSite(self),
                Filters.or(Filters.abilityLessThan(5), Filters.hasPermanentWeapon));
        if (GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTarget(game, self, cancelFilter)) {
            final TopLevelGameTextAction cancelAction = new TopLevelGameTextAction(self, gameTextSourceCardId);
            cancelAction.setText("Cancel a character's game text");
            cancelAction.appendTargeting(
                    new TargetCardOnTableEffect(cancelAction, playerId, "Choose character to cancel game text", cancelFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            cancelAction.addAnimationGroup(targetedCard);
                            cancelAction.appendCost(
                                    new UseForceEffect(cancelAction, playerId, 1));
                            cancelAction.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text",
                                    new UnrespondableEffect(cancelAction) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = cancelAction.getPrimaryTargetCard(targetGroupId);
                                            cancelAction.appendEffect(
                                                    new CancelGameTextUntilEndOfBattleEffect(cancelAction, finalTarget));
                                        }
                                    });
                        }
                    });
            actions.add(cancelAction);
        }

        return actions;
    }
}
