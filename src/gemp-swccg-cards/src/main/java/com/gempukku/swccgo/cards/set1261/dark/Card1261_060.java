package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.*;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import java.util.Collections;
import java.util.List;

/** Legacy Block 6.1 Fett's Blaster Rifle. */
public class Card1261_060 extends AbstractCharacterWeapon {
    public Card1261_060() {
        super(Side.DARK, 2, "Fett's Blaster Rifle", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("Having lost sight of Boba Fett, Luke was surprised by his sudden reappearance.");
        setGameText("Deploy on [Cloud City] or [Jabba's Palace] Boba Fett (may draw one battle destiny if not able to otherwise). Hidden Weapons is canceled. If Special Delivery not on table, may deploy it from Reserve Deck; reshuffle. May target a character for free. Draw destiny. Target hit, and forfeit = 0, if destiny +1 > defense value.");
        addIcons(Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_6);
        addKeywords(Keyword.BLASTER_RIFLE);
        addPersona(Persona.BOBA_FETTS_BLASTER_RIFLE);
        setMatchingCharacterFilter(eligibleBearer());
    }

    private Filter eligibleBearer() {
        return Filters.and(Filters.Boba_Fett, Filters.or(Icon.CLOUD_CITY, Icon.JABBAS_PALACE));
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId option, boolean asReact) {
        return Filters.and(Filters.your(self), eligibleBearer());
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(SwccgGame game, PhysicalCard self) {
        return eligibleBearer();
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, Filters.hasAttached(self), 1));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int sourceId) {
        if (TriggerConditions.isPlayingCard(game, effect, Filters.title(Title.Hidden_Weapons))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        GameTextActionId id = GameTextActionId.LEGACY__FETTS_BLASTER_RIFLE__DEPLOY_SPECIAL_DELIVERY;
        if (!GameConditions.canSpot(game, self, Filters.title(Title.Special_Delivery))
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, sourceId, id);
            action.setText("Deploy Special Delivery from Reserve Deck");
            action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Special_Delivery), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self,
            boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring,
            Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter targetFilter, boolean ignoreLimit) {
        FireWeaponActionBuilder builder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self,
                forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, targetFilter, ignoreLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (builder == null) return null;
        return Collections.singletonList(builder.buildFireWeaponWithHitAction(1, 1, Statistic.DEFENSE_VALUE, true, 0));
    }
}
