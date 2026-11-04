package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.RestoreForfeitToNormalEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveTotalAbilityReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ForfeitReducedToZeroResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Subtype: Alien
 * Title: Chewbacca, Walking Carpet
 */
public class Card1281_026 extends AbstractAlien {
    public Card1281_026() {
        super(Side.LIGHT, 2, 4, 6, 2, 6, "Chewbacca, Walking Carpet", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setArmor(4);
        setLore("Wookiee scoundrel.");
        setGameText("[Pilot] 2. Adds 2 to maneuver of any starfighter he pilots. Your total ability here may not be reduced. Once per game, may lose 1 Force to deploy a droid from Lost Pile. During battle, may target a just-hit character. Target's forfeit is restored to normal.");
        addPersona(Persona.CHEWIE);
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SCOUNDREL);
        setSpecies(Species.WOOKIEE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ManeuverModifier(self, Filters.and(Filters.starfighter, Filters.hasPiloting(self)), 2));
        modifiers.add(new MayNotHaveTotalAbilityReducedModifier(self, Filters.here(self), self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__CHEWBACCA_WALKING_CARPET__DEPLOY_DROID_FROM_LOST_PILE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a droid from Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromLostPileEffect(action, Filters.droid, false));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        // A weapon that hits and sets forfeit = 0 reduces the forfeit right after the 'hit', so the just-hit
        // character may also be targeted when its forfeit was just reduced to 0.
        PhysicalCard justHitCard = null;
        if (GameConditions.isDuringBattle(game)) {
            if (TriggerConditions.justHit(game, effectResult, Filters.character)) {
                justHitCard = ((HitResult) effectResult).getCardHit();
            }
            else if (TriggerConditions.forfeitJustReducedToZero(game, effectResult, Filters.and(Filters.character, Filters.hit))) {
                justHitCard = ((ForfeitReducedToZeroResult) effectResult).getCard();
            }
        }
        if (justHitCard != null) {
            final PhysicalCard hitCard = justHitCard;
            if (GameConditions.canTarget(game, self, hitCard)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Restore " + GameUtils.getFullName(hitCard) + "'s forfeit to normal");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose just-hit character", hitCard) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard target) {
                                action.addAnimationGroup(target);
                                // Allow response(s)
                                action.allowResponses("Restore " + GameUtils.getCardLink(target) + "'s forfeit to normal",
                                        new RespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new RestoreForfeitToNormalEffect(action, action.getPrimaryTargetCard(targetGroupId)));
                                            }
                                        });
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
