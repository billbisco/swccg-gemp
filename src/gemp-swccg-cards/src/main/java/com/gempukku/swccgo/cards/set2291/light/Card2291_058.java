package com.gempukku.swccgo.cards.set2291.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Location
 * Subtype: Site
 * Title: Geonosis: Petranaki Arena
 * Original version of 1291_58 (Virtual Block 9.1 release sheet, Jan 2014): the Dark Side raise is once per turn. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2291_058 extends AbstractSite {
    public Card2291_058() {
        super(Side.LIGHT, Title.Geonosis_Petranaki_Arena, Title.Geonosis, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLocationLightSideGameText("Amidala, Anakin, and Obi-Wan deploy -1 here.");
        setLocationDarkSideGameText("Once per turn, if you control, may raise your converted Petranaki Arena to the top.");
        setGameText("Light: Amidala, Anakin, and Obi-Wan deploy -1 here. Dark: Once per turn, if you control, may raise your converted Petranaki Arena to the top.");
        setLegacyVersion(new LegacyVersion("1291_58", "", "Virtual Block 9.1 release sheet (2014-01-10)", LocalDate.of(2014, 1, 10), LocalDate.of(2014, 2, 4)));
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.EPISODE_I, Icon.PLANET, Icon.EXTERIOR_SITE);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.or(Filters.Amidala, Filters.Anakin, Filters.ObiWan), -1, self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(final String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter converted = Filters.and(Filters.title(Title.Geonosis_Petranaki_Arena), Filters.canBeConvertedByRaisingYourLocationToTop(playerOnDarkSideOfLocation));
        if (GameConditions.isOncePerTurn(game, self, playerOnDarkSideOfLocation, gameTextSourceCardId)
                && GameConditions.controls(game, playerOnDarkSideOfLocation, self)
                && GameConditions.canTarget(game, self, converted)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId);
            action.setText("Raise converted Petranaki Arena");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerOnDarkSideOfLocation, "Choose converted Petranaki Arena", converted) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.allowResponses("Convert " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(new ConvertLocationByRaisingToTopEffect(action, targetedCard, true));
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
