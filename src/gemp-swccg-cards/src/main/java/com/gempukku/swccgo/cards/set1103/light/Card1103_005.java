package com.gempukku.swccgo.cards.set1103.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 3: Revolution
 * Type: Character
 * Subtype: Rebel
 * Title: Colonel Feyn Gospic
 */
public class Card1103_005 extends AbstractRebel {
    public Card1103_005() {
        super(Side.LIGHT, 2, 3, 2, 3, 4, "Colonel Feyn Gospic", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("One of General Dodonna's chief strategic advisors. Planned the Rebel fleet's approach to the Death Star.");
        setGameText("While Massassi Base Operations on table, opponent may deploy only one Imperial to each Yavin 4 site per turn. Doubles Rebel Tech bonus to Attack Run. Once per turn, may take Balanced Attack or a commander into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.Rebel_Tech, ModifyGameTextType.REBEL_TECH__DOUBLE_BONUS_TO_ATTACK_RUN));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__COLONEL_FEYN_GOSPIC_V__TAKE_CARD;

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take Balanced Attack or a commander into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.title("Balanced Attack"), Filters.commander), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        if (GameConditions.canSpot(game, self, Filters.Massassi_Base_Operations)
                && TriggerConditions.justDeployedToLocation(game, effectResult, opponent, Filters.Imperial, Filters.Yavin_4_site)) {
            PlayCardResult playCardResult = (PlayCardResult) effectResult;
            PhysicalCard location = playCardResult.getToLocation();
            if (location == null) {
                return null;
            }

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Limit Imperials at this site");
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action,
                            new MayNotDeployToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.Imperial), Filters.sameCardId(location)),
                            "Opponent may not deploy another Imperial to " + GameUtils.getCardLink(location) + " this turn"));
            return Collections.singletonList(action);
        }
        return null;
    }
}
