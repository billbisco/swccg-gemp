package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitValueToUseWhenForfeitedModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitedToUsedPileModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NoForceLossFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Objective
 * Title: Republic At War / Aggressive Negotiations
 */
public class Card1291_069 extends AbstractObjective {
    public static final String TITLE = "Republic At War";
    public static final String PRINTED_GAMETEXT = "Republic At War: Deploy Forward Command Center and Begun, The Clone War Has. For remainder of game, Rebels and Rebel starships are lost. You may forfeit non-lightsaber weapons (using forfeit value = 3); place your just forfeited weapon in Used Pile. Your Destiny is suspended. You lose no Force to Vengeance Of The Dark Prince. While this side up, once per turn, may deploy an [Episode I] battleground from Reserve Deck; reshuffle. Flip this card if a complete [Clone Army] Mission on table. Aggressive Negotiations: Immediately, may place X cards from hand on bottom of Force Pile to draw up to X+1 cards from Reserve Deck once per game. While this side up, opponent's battle destiny draws are -1. During your control phase, may use 2 Force to take a [Clone Army] character, starship or vehicle into hand from Reserve Deck; reshuffle. Your total power in battles is +Y, and whenever you win a battle, retrieve up to Y Force, where Y = the number of complete [Clone Army] Missions on table. Flip this card if no complete [Clone Army] Missions on table.";

    public Card1291_069() {
        super(Side.LIGHT, 0, TITLE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setGameText(PRINTED_GAMETEXT);
        addIcons(Icon.EPISODE_I);
    }

    /**
     * A 'complete' [Clone Army] Mission: a Mission completes either by stacking a card on itself (Assault On
     * Muunilinst, Hunt Down The Dark Acolyte) or by recording completion in its while-in-play data (Rescue On Geonosis).
     */
    static Filter completeCloneArmyMission() {
        return Filters.and(CardType.MISSION, Icon.CLONE_ARMY, new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getWhileInPlayData() != null
                        || !gameState.getStackedCards(physicalCard).isEmpty();
            }
        });
    }

    static Filter remainderLostFilter() {
        return Filters.or(Filters.Rebel, Filters.Rebel_starship);
    }

    static Filter forfeitWeapons(String playerId) {
        return Filters.and(Filters.your(playerId), Filters.weapon, Filters.not(Filters.lightsaber));
    }

    static List<Modifier> remainderOfGameModifiers(PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        // "using forfeit value = 3": also lets weapons with no forfeit attribute (e.g. character weapons) be forfeited
        modifiers.add(new ForfeitValueToUseWhenForfeitedModifier(self, forfeitWeapons(playerId), 3));
        modifiers.add(new ForfeitedToUsedPileModifier(self, forfeitWeapons(playerId)));
        modifiers.add(new SuspendsCardModifier(self, Filters.Your_Destiny));
        modifiers.add(new NoForceLossFromCardModifier(self, Filters.title(Title.Vengeance_Of_The_Dark_Prince), playerId));
        return modifiers;
    }

    static RequiredGameTextTriggerAction makeRemainderLostAction(SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        Collection<PhysicalCard> toLose = Filters.filterActive(game, self, remainderLostFilter());
        if (toLose.isEmpty()) {
            return null;
        }
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setSingletonTrigger(true);
        action.setText("Make Rebels and Rebel starships lost");
        action.setActionMsg("Make " + GameUtils.getAppendedNames(toLose) + " lost");
        action.appendEffect(new LoseCardsFromTableEffect(action, toLose));
        return action;
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.title(Card1291_057.TITLE), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Forward Command Center to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.title(Card1291_050.TITLE), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Begun, The Clone War Has to deploy";
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        final int permCardId = self.getPermanentCardId();
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameActionProxyEffect(action,
                        new AbstractActionProxy() {
                            @Override
                            public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                PhysicalCard self = game.findCardByPermanentId(permCardId);
                                if (TriggerConditions.isTableChanged(game, effectResult)) {
                                    RequiredGameTextTriggerAction loseAction = makeRemainderLostAction(game, self, gameTextSourceCardId);
                                    if (loseAction != null) {
                                        actions.add(loseAction);
                                    }
                                }
                                return actions;
                            }
                        }));
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return remainderOfGameModifiers(self);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__REPUBLIC_AT_WAR__DEPLOY_BATTLEGROUND;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy [Episode I] battleground from Reserve Deck");
            action.setActionMsg("Deploy an [Episode I] battleground from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Icon.EPISODE_I, Filters.battleground), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            RequiredGameTextTriggerAction loseAction = makeRemainderLostAction(game, self, gameTextSourceCardId);
            if (loseAction != null) {
                actions.add(loseAction);
            }
            if (GameConditions.canBeFlipped(game, self)
                    && GameConditions.canSpot(game, self, completeCloneArmyMission())) {
                RequiredGameTextTriggerAction flip = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                flip.setSingletonTrigger(true);
                flip.setText("Flip");
                flip.setActionMsg(null);
                flip.appendEffect(new FlipCardEffect(flip, self));
                actions.add(flip);
            }
        }
        return actions;
    }
}
