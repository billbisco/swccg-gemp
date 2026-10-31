package com.gempukku.swccgo.cards.set1109.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 9
 * Type: Effect
 * Title: Well-earned Command
 */
public class Card1109_049 extends AbstractNormalEffect {
    public Card1109_049() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Well_earned_Command, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("Imperial officers often simulate large-scale battles with hologram games to improve their tactical ability. Those who excel at these games often mark themselves for advancement.");
        setGameText("Deploy on a battleground site. At sites where you have a vehicle with armor and an Imperial general (or Imperial commander), your total battle destiny is +3 and opponent may not draw more than two battle destiny. If opponent controls this site, this Effect lost.");
        addIcons(Icon.ENDOR);
        addKeywords(Keyword.DEPLOYS_ON_SITE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.battleground_site;
    }

    private Filter armoredVehicleAndImperialLeaderSites(PhysicalCard self) {
        Filter yourArmoredVehicle = Filters.and(Filters.your(self), Filters.vehicle, Filters.hasArmor);
        Filter yourImperialGeneralOrCommander = Filters.and(Filters.your(self), Filters.Imperial, Filters.or(Filters.general, Filters.commander));
        return Filters.and(Filters.site, Filters.sameSiteAs(self, yourArmoredVehicle), Filters.sameSiteAs(self, yourImperialGeneralOrCommander));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter sites = armoredVehicleAndImperialLeaderSites(self);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalBattleDestinyModifier(self, sites, 3, playerId));
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, sites, 2, opponent));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.controls(game, opponent, Filters.sameLocation(self))) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make this Effect lost");
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
