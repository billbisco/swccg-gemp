package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.effects.CancelWeaponTargetingEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInLostPileFromTableEffect;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Solomahal
 */
public class Card1210_139 extends AbstractNormalEffect {
    public Card1210_139() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, "Solomahal", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Veteran officer of the Old Republic who retired from active duty after the Clone Wars. Makes a living in the Outer Rim Territories passing on his scouting expertise.");
        setGameText("Deploy on your scout (except Luke). If scout (or starship they are aboard) was just targeted by a weapon, may place this Effect in Lost Pile to cancel that targeting.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.scout, Filters.except(Filters.Luke));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.and(Filters.scout, Filters.except(Filters.Luke));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard scout = self.getAttachedTo();
        if (scout == null) {
            return null;
        }
        Filter scoutOrStarshipAboard = Filters.or(Filters.sameCardId(scout), Filters.and(Filters.starship, Filters.hasAboard(scout)));
        if (TriggerConditions.isTargetedByWeapon(game, effect, scoutOrStarshipAboard, Filters.weapon)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel weapon targeting");
            action.appendCost(
                    new PlaceCardInLostPileFromTableEffect(action, self));
            action.appendEffect(
                    new CancelWeaponTargetingEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
