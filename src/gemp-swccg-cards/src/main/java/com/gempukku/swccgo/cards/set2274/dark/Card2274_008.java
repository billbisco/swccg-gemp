package com.gempukku.swccgo.cards.set2274.dark;

import com.gempukku.swccgo.common.LegacyVersion;
import java.time.LocalDate;
import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/** Original Shadows Of The Empire (Virtual Block 7.4 sheet, 2011-12-16), a version of 1274_8; replaced by the PC card-list rephrase (PC-E1, by 2012-07-21). Same play. Anything Goes only (docs/legacy-versions.md). */
public class Card2274_008 extends AbstractNormalEffect {
    public Card2274_008() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, "Shadows Of The Empire", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_4, Rarity.V);
        setLore("Imposing. Ominous. Intimidating. Instrument for the evil Emperor's sinister reach across the galaxy. Used on a secret frequency of the Imperial HoloNet.");
        setGameText("Deploy on Agents Of Black Sun. Mistryl are lost. Once per game, may [upload] Secret Plans. May use 3 Force to [download] Imperial Square, and once per turn, if Emperor Palpatine there, may draw a card from Force Pile (and if you occupy three battlegrounds, opponent loses 1 Force). [Immune to Alter.]");
        setLegacyVersion(new LegacyVersion("1274_8", "",
                "Virtual Block 7.4 release sheet (2011-12-16)", LocalDate.of(2011, 12, 16), LocalDate.of(2012, 7, 21)));
        addIcons(Icon.DAGOBAH);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Agents_Of_Black_Sun;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        // Stays on the Objective after it flips
        return Filters.or(Filters.Objective, Filters.Agents_Of_Black_Sun);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            Collection<PhysicalCard> mistryl = Filters.filterActive(game, self, Filters.and(Filters.Mistryl, Filters.canBeTargetedBy(self)));
            if (!mistryl.isEmpty()) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make Mistryl lost");
                action.appendEffect(
                        new LoseCardsFromTableEffect(action, mistryl));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__SHADOWS_OF_THE_EMPIRE_V74__TAKE_SECRET_PLANS;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Secret Plans into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Secret_Plans, true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.LEGACY__SHADOWS_OF_THE_EMPIRE_V74__DEPLOY_IMPERIAL_SQUARE;
        if (GameConditions.canUseForce(game, playerId, 3)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Coruscant_Imperial_Square)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Imperial Square from Reserve Deck");
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Coruscant_Imperial_Square, true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.LEGACY__SHADOWS_OF_THE_EMPIRE_V74__DRAW_FROM_FORCE_PILE;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasForcePile(game, playerId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.Emperor, Filters.at(Filters.Coruscant_Imperial_Square)))) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw a card from Force Pile");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new DrawCardIntoHandFromForcePileEffect(action, playerId) {
                        @Override
                        protected void cardDrawnIntoHand(PhysicalCard card) {
                            if (card != null && GameConditions.occupies(game, playerId, 3, Filters.battleground)) {
                                action.appendEffect(
                                        new LoseForceEffect(action, game.getOpponent(playerId), 1));
                            }
                        }
                    });
            actions.add(action);
        }
        return actions;
    }
}
