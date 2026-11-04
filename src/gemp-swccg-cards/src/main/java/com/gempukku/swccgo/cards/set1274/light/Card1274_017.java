package com.gempukku.swccgo.cards.set1274.light;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInLostPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.GainsPresenceIconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.4
 * Type: Device
 * Title: Motivator
 */
public class Card1274_017 extends AbstractDevice {
    private static final String WHATRE_YOU_TRYIN_TO_PUSH_ON_US = "What're You Tryin' To Push On Us?";

    public Card1274_017() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, "Motivator", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_4, Rarity.V);
        setGameText("Deploy on your non-[Reflections 2] astromech droid. Droid is power +2, gains [Presence Droid], and if present at a site, draws one battle destiny if unable to otherwise. During any move phase, may place this device in Lost Pile to take one What're You Tryin' To Push On Us? or R2-D2 into hand from Reserve Deck; reshuffle.");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.astromech_droid, Filters.not(Icon.REFLECTIONS_II));
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.astromech_droid;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.astromech_droid, Filters.not(Icon.REFLECTIONS_II));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter droid = Filters.hasAttached(self);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, droid, 2));
        modifiers.add(new GainsPresenceIconModifier(self, droid));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, Filters.and(droid, Filters.presentAt(Filters.site)), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__MOTIVATOR__UPLOAD_CARD;

        // Check condition(s)
        if (GameConditions.isDuringEitherPlayersPhase(game, Phase.MOVE)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place in Lost Pile to take card into hand");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " in Lost Pile to take What're You Tryin' To Push On Us? or R2-D2 into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new PlaceCardInLostPileFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId,
                            Filters.or(Filters.title(WHATRE_YOU_TRYIN_TO_PUSH_ON_US), Filters.R2D2), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
