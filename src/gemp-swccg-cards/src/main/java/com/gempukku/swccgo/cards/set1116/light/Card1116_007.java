package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ReleaseCaptiveEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Location
 * Subtype: Site
 * Title: Blockade Flagship: Prison
 */
public class Card1116_007 extends AbstractUniqueStarshipSite {
    public Card1116_007() {
        super(Side.LIGHT, "Blockade Flagship: Prison", Persona.BLOCKADE_FLAGSHIP, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("If you occupy during your control phase, may release an imprisoned captive here. Immune to Revolution.");
        setLocationDarkSideGameText("While Palpatine imprisoned here, opponent's Force drains everywhere are -1.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.ENDOR, Icon.EPISODE_I, Icon.INTERIOR_SITE, Icon.STARSHIP_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Title.Revolution));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        String opponent = game.getOpponent(playerOnDarkSideOfLocation);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.location, new HereCondition(self, Filters.and(Filters.imprisoned, Filters.titleContains("Palpatine"))), -1, opponent));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isDuringYourPhase(game, playerOnLightSideOfLocation, Phase.CONTROL)
                && GameConditions.occupies(game, playerOnLightSideOfLocation, self)
                && GameConditions.canSpot(game, self, Filters.and(Filters.imprisoned, Filters.here(self)))) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId);
            action.setText("Release an imprisoned captive");
            action.appendTargeting(new ChooseCardOnTableEffect(action, playerOnLightSideOfLocation, "Choose captive to release",
                    Filters.and(Filters.imprisoned, Filters.here(self))) {
                @Override
                protected void cardSelected(PhysicalCard selectedCard) {
                    action.appendEffect(new ReleaseCaptiveEffect(action, selectedCard));
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
