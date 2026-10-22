package com.gempukku.swccgo.cards.set1291.dark;

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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitedToUsedPileModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitValueToUseWhenForfeitedModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Objective
 * Title: Separatist Uprising / At War With Itself
 */
public class Card1291_029 extends AbstractObjective {
    public static final String PACK_GAMETEXT = "Separatist Uprising: Deploy Separatist Council Room and War Has Begun. For remainder of game, Galen, Imperials, and Imperial starships are lost. Your Republic characters and [Episode I] vehicles gain [Separatist]. You may forfeit non-lightsaber weapons (using forfeit value = 3); place your just forfeited weapon in Used Pile. Once per turn, may use 1 Force to deploy an [Episode I] battleground from your reserve deck; reshuffle. Flip this card if you have one 'complete' [Separatist] Mission on table. At War With Itself:Immediately, may retrieve up to 3 Force (Force retrieved in this way may be taken into hand) once per game. While this side up, opponent's battle destiny draws are -1. Once during your control phase, may use 2 Force to take into hand a [Separatist] character, vehicle, or starship from reserve deck; reshuffle. Your total power in battles is +X, and whenever you win a battle, opponent loses X Force, where X = the number of 'complete' [Separatist] Missions on table. Flip this card if you have no 'complete' [Separatist] Missions on table.";

    public Card1291_029() {
        super(Side.DARK, 0, "Separatist Uprising", ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setGameText(PACK_GAMETEXT);
        addIcons(Icon.EPISODE_I);
    }

    static Filter completeSeparatistMission() {
        return Filters.and(CardType.MISSION, Icon.SEPARATIST, Filters.hasStacked(Filters.any));
    }

    static Filter remainderLostFilter() {
        return Filters.or(Filters.title("Galen"), Filters.Imperial, Filters.Imperial_starship);
    }

    static Filter grantSeparatist(String playerId) {
        return Filters.and(Filters.your(playerId), Filters.or(
                Filters.Republic_character,
                Filters.and(Filters.vehicle, Icon.EPISODE_I)));
    }

    static Filter forfeitWeapons(String playerId) {
        return Filters.and(Filters.your(playerId), Filters.weapon, Filters.not(Filters.lightsaber));
    }

    static List<Modifier> remainderOfGameModifiers(PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IconModifier(self, grantSeparatist(playerId), Icon.SEPARATIST));
        // "using forfeit value = 3": also lets weapons with no forfeit attribute (e.g. character weapons) be forfeited
        modifiers.add(new ForfeitValueToUseWhenForfeitedModifier(self, forfeitWeapons(playerId), 3));
        modifiers.add(new ForfeitedToUsedPileModifier(self, forfeitWeapons(playerId)));
        return modifiers;
    }

    static RequiredGameTextTriggerAction makeRemainderLostAction(SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        Collection<PhysicalCard> toLose = Filters.filterActive(game, self, remainderLostFilter());
        if (toLose.isEmpty()) {
            return null;
        }
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setSingletonTrigger(true);
        action.setText("Make Galen, Imperials, and Imperial starships lost");
        action.setActionMsg("Make " + GameUtils.getAppendedNames(toLose) + " lost");
        action.appendEffect(new LoseCardsFromTableEffect(action, toLose));
        return action;
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Geonosis_Separatist_Council_Room), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Separatist Council Room to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.title(Title.War_Has_Begun), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose War Has Begun to deploy";
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
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__SEPARATIST_UPRISING__DEPLOY_BATTLEGROUND;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy [Episode I] battleground from Reserve Deck");
            action.setActionMsg("Deploy an [Episode I] battleground from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(new UseForceEffect(action, playerId, 1));
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
                    && GameConditions.canSpot(game, self, completeSeparatistMission())) {
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
