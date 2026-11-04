package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeathMarkTotalModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Objective
 * Title: Contract Killers / Feared Throughout The Galaxy
 */
public class Card1281_024 extends AbstractObjective {
    public static final String SHEET_GAMETEXT = "Contract Killers: Deploy Coruscant system, Sub City Lair and On The Hunt. For remainder of game, Aurra, Galen, Mara, and your aliens with 'plotting to kill Jabba' in lore are assassins. Your cards with ability are deploy +3 except assassins and starships. Characters add 1 to their Death Mark total for each weapon or device they carry. Once per turn, may deploy a Coruscant battleground from Reserve Deck; reshuffle. Flip this card if assassins control two battlegrounds (unless opponent's non-droid character at a battleground site) or you just drew destiny for Death Mark. Feared Throughout The Galaxy: While this side up, during your control phase, opponent loses 1 Force. If two assassins (except Galen) are in battle, add one battle destiny. Once per turn, if opponent just lost a character, an assassin there may gain 'reputation;' retrieve any one card (may only be canceled by Aim High) and stack it face down on that assassin. Assassins with 'reputation' may add 1 to Force drains where present. Flip this card at the end of any turn if opponent's non-droid character at a battleground site (unless Death Mark is on your side of the table).";

    public Card1281_024() {
        super(Side.DARK, 0, "Contract Killers", ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setGameText(SHEET_GAMETEXT);
        addIcons(Icon.CLOUD_CITY);
    }

    /**
     * (Virtual Block 8) Death Mark.
     */
    static Filter deathMark() {
        return Filters.and(Filters.title("Death Mark"), Icon.LEGACY_BLOCK_8);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Coruscant_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Coruscant system to deploy";
                    }

                    @Override
                    protected void cardDeployed(PhysicalCard card) {
                        action.appendRequiredEffect(
                                new DeployCardToSystemFromReserveDeckEffect(action, Filters.title("Coruscant: Sub City Lair"), Title.Coruscant, true, false) {
                                    @Override
                                    public String getChoiceText() {
                                        return "Choose Sub City Lair to deploy";
                                    }
                                });
                        action.appendRequiredEffect(
                                new DeployCardFromReserveDeckEffect(action, Filters.title("On The Hunt"), true, false) {
                                    @Override
                                    public String getChoiceText() {
                                        return "Choose On The Hunt to deploy";
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
                new AddUntilEndOfGameModifierEffect(action,
                        new KeywordModifier(self, Filters.or(Filters.Aurra, Filters.Galen, Filters.Mara_Jade,
                                Filters.and(Filters.your(playerId), Filters.alien, Filters.loreContains("plotting to kill Jabba"))), Keyword.ASSASSIN), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new DeployCostModifier(self, Filters.and(Filters.your(playerId), Filters.hasAbilityOrHasPermanentPilotWithAbility,
                                Filters.not(Filters.or(Filters.assassin, Filters.starship))), 3), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new DeathMarkTotalModifier(self, Filters.character), null));
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__CONTRACT_KILLERS__DEPLOY_CORUSCANT_BATTLEGROUND;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a Coruscant battleground from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Coruscant_location, Filters.battleground), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (GameConditions.canBeFlipped(game, self)
                && ((TriggerConditions.isTableChanged(game, effectResult) && assassinsControlTwoBattlegrounds(game, self))
                || TriggerConditions.isDestinyJustDrawnFor(game, effectResult, playerId, deathMark()))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    private static boolean assassinsControlTwoBattlegrounds(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        return Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground, Filters.controlsWith(playerId, self, Filters.assassin))) >= 2
                && !opponentsNonDroidCharacterAtBattlegroundSite(game, self);
    }

    static boolean opponentsNonDroidCharacterAtBattlegroundSite(SwccgGame game, PhysicalCard self) {
        return GameConditions.canSpot(game, self, Filters.and(Filters.opponents(self), Filters.non_droid_character, Filters.at(Filters.battleground_site)));
    }

    static boolean deathMarkOnYourSideOfTable(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        for (PhysicalCard card : Filters.filterAllOnTable(game, deathMark())) {
            if (card.getZone() == Zone.SIDE_OF_TABLE && playerId.equals(card.getZoneOwner())) {
                return true;
            }
        }
        return false;
    }
}
