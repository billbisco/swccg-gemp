package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Objective
 * Title: Hidden Base / Systems Will Slip Through Your Fingers
 */
public class Card1115_012 extends AbstractObjective {
    public static final String PACK_GAMETEXT = "Hidden Base: Deploy a battleground system (with Hidden Landing Site there) and Uncharted Settlements. Choose: Rebel/Rebel starship, Alien/[Independent Starship] starship, Republic/[Republic] starship. {For} remainder of game, you may not deploy cards with ability except vehicles, Jedi Masters, and the pair you chose. Once per turn, may deploy a battleground system from Reserve Deck; reshuffle. {While} this side up, once per turn, may deploy from Reserve Deck a Rebel Cell site and/or a character of ability 2 or 3 to a Rebel Cell site; reshuffle. {Flip} this card if you occupy three Rebel Cell sites (and opponent controls no Rebel Cell sites). Systems Will Slip Through Your Fingers (V): {While} this side up, your Force drains are +1 at each battleground system. Once per turn, may lose 1 Force to cancel a Force drain at an opponent's location. At Rebel Cell sites, sectors, and each battleground system: your characters of ability < 4, [Hoth] artillery weapons, and starships are immune to attrition < 4 (or add 2 to immunity). Koensayr Manufacturing and Incom Corporation instead affect all your non-unique starships (except squadrons and corvettes) and T-47s. {Flip} this card if opponent controls two Rebel Cell sites.";

    public Card1115_012() {
        super(Side.LIGHT, 0, Title.Hidden_Base, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setFrontOfDoubleSidedCard(true);
        setGameText(PACK_GAMETEXT);
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        Filter startingSystem = Filters.and(Filters.battleground_system, Filters.planet_system,
                Filters.not(Filters.or(Filters.title(Title.Bespin), Filters.title(Title.Coruscant),
                        Filters.title(Title.Dagobah), Filters.title(Title.Dantooine),
                        Filters.title(Title.Endor), Filters.title(Title.Hoth))));
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, startingSystem, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose battleground system to deploy";
                    }

                    @Override
                    protected void cardDeployed(PhysicalCard card) {
                        String systemName = card.getBlueprint().getSystemName();
                        action.appendRequiredEffect(
                                new DeployCardToSystemFromReserveDeckEffect(action, Filters.and(Filters.titleContains("Hidden Landing Site"), new Filter() {
                                    @Override
                                    public boolean accepts(com.gempukku.swccgo.game.state.GameState gameState, com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                                        return physicalCard.getBlueprint().getExpansionSet() == ExpansionSet.VIRTUAL_ORIGINAL_SET_15;
                                    }
                                }), systemName, true, false) {
                                    @Override
                                    public String getChoiceText() {
                                        return "Choose Hidden Landing Site to deploy";
                                    }
                                });
                        action.appendRequiredEffect(
                                new DeployCardFromReserveDeckEffect(action, Filters.title("Uncharted Settlements"), true, false) {
                                    @Override
                                    public String getChoiceText() {
                                        return "Choose Uncharted Settlements to deploy";
                                    }
                                });
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new PlayoutDecisionEffect(action, playerId,
                        new MultipleChoiceAwaitingDecision("Choose pair you may deploy", new String[]{
                                "Rebel / Rebel starship",
                                "Alien / Independent starship",
                                "Republic / Republic starship"
                        }) {
                            @Override
                            protected void validDecisionMade(int index, String result) {
                                String pairKey;
                                Filter pairFilter;
                                if (index == 0) {
                                    pairKey = "Rebel";
                                    pairFilter = Filters.or(Filters.Rebel, Filters.Rebel_starship);
                                }
                                else if (index == 1) {
                                    pairKey = "Alien";
                                    pairFilter = Filters.or(Filters.alien, Filters.and(Filters.starship, Icon.INDEPENDENT));
                                }
                                else {
                                    pairKey = "Republic";
                                    pairFilter = Filters.or(Filters.Republic, Filters.Republic_starship);
                                }
                                action.appendEffect(
                                        new SetWhileInPlayDataEffect(action, self, new WhileInPlayData(pairKey)));
                                action.appendEffect(
                                        new AddUntilEndOfGameModifierEffect(action,
                                                new MayNotDeployModifier(self, Filters.and(
                                                        Filters.hasAbilityOrHasPermanentPilotWithAbility,
                                                        Filters.not(Filters.or(Filters.vehicle, Filters.Jedi_Master, pairFilter))), playerId), null));
                            }
                        }));
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId battlegroundActionId = GameTextActionId.LEGACY__HIDDEN_BASE_V__DOWNLOAD_BATTLEGROUND_SYSTEM;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, battlegroundActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, battlegroundActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, battlegroundActionId);
            action.setText("Deploy battleground system from Reserve Deck");
            action.setActionMsg("Deploy a battleground system from Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.battleground_system, true));
            actions.add(action);
        }

        GameTextActionId siteActionId = GameTextActionId.LEGACY__HIDDEN_BASE_V__DEPLOY_REBEL_CELL_SITE;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, siteActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, siteActionId)) {
            TopLevelGameTextAction siteAction = new TopLevelGameTextAction(self, gameTextSourceCardId, siteActionId);
            siteAction.setText("Deploy Rebel Cell site from Reserve Deck");
            siteAction.setActionMsg("Deploy a Rebel Cell site from Reserve Deck");
            siteAction.appendUsage(
                    new OncePerTurnEffect(siteAction));
            siteAction.appendEffect(
                    new DeployCardFromReserveDeckEffect(siteAction, Filters.and(Filters.site, Filters.titleContains("Rebel Cell")), true));
            actions.add(siteAction);
        }

        GameTextActionId characterActionId = GameTextActionId.LEGACY__HIDDEN_BASE_V__DEPLOY_CHARACTER_TO_REBEL_CELL_SITE;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, characterActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, characterActionId)) {
            TopLevelGameTextAction characterAction = new TopLevelGameTextAction(self, gameTextSourceCardId, characterActionId);
            characterAction.setText("Deploy character of ability 2 or 3 to a Rebel Cell site");
            characterAction.setActionMsg("Deploy a character of ability 2 or 3 to a Rebel Cell site from Reserve Deck");
            characterAction.appendUsage(
                    new OncePerTurnEffect(characterAction));
            characterAction.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(characterAction,
                            Filters.and(Filters.character, Filters.or(Filters.abilityEqualTo(2), Filters.abilityEqualTo(3))),
                            Filters.titleContains("Rebel Cell"), true));
            actions.add(characterAction);
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.occupies(game, playerId, 3, Filters.titleContains("Rebel Cell"))
                && !GameConditions.controls(game, opponent, Filters.titleContains("Rebel Cell"))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return java.util.Collections.singletonList(action);
        }
        return null;
    }
}
