package com.gempukku.swccgo.cards.set1105.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 5: A Dark Time
 * Type: Effect
 * Title: Bad Feeling Have I
 */
public class Card1105_028 extends AbstractNormalEffect {
    public Card1105_028() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Bad_Feeling_Have_I, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Ready are you? What know you of ready?'");
        setGameText("Deploy on table. You may not initiate lightsaber combat. Once per game, may deploy Vader from Reserve Deck (deploy +2); reshuffle. May use 2 Force to take a card (without ability) with \"duel\" in game text into hand from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.DAGOBAH);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SpecialFlagModifier(self, ModifierFlag.MAY_NOT_INITIATE_LIGHTSABER_COMBAT, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId deployVaderId = GameTextActionId.OVS__BAD_FEELING_HAVE_I__DEPLOY_VADER;
        if (GameConditions.isOncePerGame(game, self, deployVaderId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployVaderId, Persona.VADER)) {
            final TopLevelGameTextAction deployVader = new TopLevelGameTextAction(self, gameTextSourceCardId, deployVaderId);
            deployVader.setText("Deploy Vader from Reserve Deck");
            deployVader.appendUsage(
                    new OncePerGameEffect(deployVader));
            deployVader.appendEffect(
                    new DeployCardFromReserveDeckEffect(deployVader, Filters.Vader, 2, true));
            actions.add(deployVader);
        }

        GameTextActionId uploadId = GameTextActionId.OVS__BAD_FEELING_HAVE_I__UPLOAD_DUEL_CARD;
        Filter duelCard = Filters.and(Filters.not(Filters.hasAbility), Filters.gameTextContains("duel"));
        if (GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, uploadId)) {
            final TopLevelGameTextAction upload = new TopLevelGameTextAction(self, gameTextSourceCardId, uploadId);
            upload.setText("Take a duel card into hand from Reserve Deck");
            upload.appendCost(
                    new UseForceEffect(upload, playerId, 2));
            upload.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(upload, playerId, duelCard, true));
            actions.add(upload);
        }

        return actions;
    }
}
