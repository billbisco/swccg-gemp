package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RearmCharacterEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitValueReducedModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * Armed And Dangerous & Krayt Dragon Howl, final Light master page 32.
 * Text: PC-E1 = the release-sheet text under the title Armed And Dangerous & Krayt Dragon Howl (retitled 2011-07-14,
 * PC forum p636351; Master List slip and PC card image). The card list's written-out icons are notation, not a version
 * (Bill 2026-11-11). The printing titled Krayt Dragon Howl & Armed And Dangerous is 2261_93 (legacy batch B41).
 */
public class Card1261_093 extends AbstractUsedOrLostInterrupt {
    private static final Filter CANCEL = Filters.or(Filters.title(Title.Disarmed), Filters.Stunning_Leader);
    private static final Filter OBI_WAN = Filters.and(Filters.ObiWan, Filters.not(Icon.REFLECTIONS_III));
    private static final GameTextActionId WEAPON = GameTextActionId.LEGACY__ARMED_AND_DANGEROUS__DEPLOY_WEAPON;
    private static final GameTextActionId OBI = GameTextActionId.LEGACY__KRAYT_DRAGON_HOWL__DEPLOY_OR_RETRIEVE_OBI_WAN;

    public Card1261_093() {
        super(Side.LIGHT, 4, "Armed And Dangerous & Krayt Dragon Howl", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setTitleComponents(new TitleComponent("Armed And Dangerous", Uniqueness.UNIQUE), new TitleComponent("Krayt Dragon Howl", Uniqueness.UNIQUE));
        setGameText("USED: Cancel Disarmed or Stunning Leader. OR If a battle was just initiated, ▼ (or deploy from hand) a character weapon on your participating character. LOST: Use 1 Force to ▼ (or retrieve) non-[Reflections 3] Obi-Wan. OR If opponent just initiated a battle against non-[Reflections 3] Obi-Wan alone at a site, his forfeit cannot be reduced.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        if (!TriggerConditions.isPlayingCard(game, effect, CANCEL) || !GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            return null;
        }
        PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
        CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
        return List.of(action);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();
        if (GameConditions.canTarget(game, self, Filters.Disarmed)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel Disarmed");
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose disarmed character", Filters.Disarmed) {
                @Override
                protected void cardTargeted(int group, PhysicalCard target) {
                    resolve(action, new RearmCharacterEffect(action, target));
                }
            });
            actions.add(action);
        }
        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, OBI, Persona.OBIWAN)) {
                PlayInterruptAction action = new PlayInterruptAction(game, self, OBI, CardSubtype.LOST);
                action.setText("Deploy Obi-Wan from Reserve Deck");
                action.appendCost(new UseForceEffect(action, playerId, 1));
                resolve(action, new DeployCardFromReserveDeckEffect(action, OBI_WAN, true));
                actions.add(action);
            }
            if (GameConditions.canSearchLostPile(game, playerId, self, OBI)) {
                PlayInterruptAction action = new PlayInterruptAction(game, self, OBI, CardSubtype.LOST);
                action.setText("Retrieve Obi-Wan");
                action.appendCost(new UseForceEffect(action, playerId, 1));
                resolve(action, new RetrieveCardEffect(action, playerId, OBI_WAN));
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult result, PhysicalCard self) {
        if (!TriggerConditions.battleInitiated(game, result)) {
            return null;
        }
        List<PlayInterruptAction> actions = new ArrayList<>();
        Filter participants = Filters.and(Filters.your(self), Filters.character, Filters.participatingInBattle);
        if (GameConditions.canSpot(game, self, participants)) {
            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, WEAPON, true, false)) {
                PlayInterruptAction action = new PlayInterruptAction(game, self, WEAPON, CardSubtype.USED);
                action.setText("Deploy a character weapon from Reserve Deck");
                resolve(action, new DeployCardToTargetFromReserveDeckEffect(action, Filters.character_weapon, participants, true));
                actions.add(action);
            }
            Filter deployable = Filters.and(Filters.character_weapon, Filters.deployableToTarget(self, participants, false, 0));
            if (GameConditions.hasInHand(game, playerId, deployable)) {
                PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Deploy a character weapon from hand");
                resolve(action, new DeployCardToTargetFromHandEffect(action, playerId, Filters.character_weapon, participants));
                actions.add(action);
            }
        }
        Filter defender = Filters.and(participants, OBI_WAN, Filters.alone, Filters.at(Filters.site));
        if (TriggerConditions.battleInitiated(game, result, game.getOpponent(playerId))
                && GameConditions.canTarget(game, self, defender)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Protect Obi-Wan's forfeit");
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose Obi-Wan to protect his forfeit", defender) {
                @Override
                protected void cardTargeted(int group, PhysicalCard target) {
                    resolve(action, new AddUntilEndOfBattleModifierEffect(action, new MayNotHaveForfeitValueReducedModifier(self, target),
                            "Obi-Wan's forfeit cannot be reduced"));
                }
            });
            actions.add(action);
        }
        return actions;
    }

    private void resolve(PlayInterruptAction action, StandardEffect effect) {
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                action.appendEffect(effect);
            }
        });
    }
}
