package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitValueReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Title: Den Of Thieves
 */
public class Card1230_027 extends AbstractNormalEffect {
    public Card1230_027() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Den_Of_Thieves, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Jabba's Palace is considered a safe haven to many on the run. It is also widely known to provide luxurious accommodations to its welcomed guests.");
        setGameText("Deploy on table. Your aliens of ability < 3 may not have their forfeit values reduced. Your battle destiny draws are +1 where you have a character with 'plotting to kill Jabba' in lore. May lose 1 Force to raise your converted [Jabba's Palace] site to the top.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotHaveForfeitValueReducedModifier(self, Filters.and(Filters.your(self), Filters.alien, Filters.abilityLessThan(3))));
        modifiers.add(new EachBattleDestinyModifier(self,
                Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.character, Filters.loreContains("plotting to kill Jabba"))),
                1, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__DEN_OF_THIEVES_V__RAISE_SITE;
        Filter yourJPSiteFilter = Filters.and(Filters.your(playerId), Icon.JABBAS_PALACE, Filters.site);
        Filter convertableTargetsFilter = Filters.and(Filters.canBeConvertedByRaisingYourLocationToTop(playerId), Filters.convertedLocationOnTopOfLocation(yourJPSiteFilter));
        if (GameConditions.canTarget(game, self, convertableTargetsFilter)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Raise your converted Jabba's Palace site");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target site to convert", convertableTargetsFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.appendCost(
                                    new LoseForceEffect(action, playerId, 1));
                            action.allowResponses("Convert " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(
                                                    new ConvertLocationByRaisingToTopEffect(action, targetedCard, true));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
