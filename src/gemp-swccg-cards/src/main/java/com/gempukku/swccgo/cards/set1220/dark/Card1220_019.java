package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitValueReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Alien
 * Title: Zuckuss
 */
public class Card1220_019 extends AbstractAlien {
    public Card1220_019() {
        super(Side.DARK, 1, 3, 2, 4, 4, "Zuckuss", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Male Gand. Practitioner of ancient religious findsman vocation. Bounty hunter and scout. Gains surprisingly accurate information through mystical visions during meditation.");
        setGameText("Adds 2 to power of anything he pilots. Mist Hunter is maneuver +1. Your Black Sun agents at same and related sites may not have their forfeit reduced. Once per game, may deploy non-[Permanent Weapon] 4-LOM here from Reserve Deck; reshuffle. Immune to attrition < 3.");
        addPersona(Persona.ZUCKUSS);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.DAGOBAH);
        addKeywords(Keyword.BOUNTY_HUNTER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ManeuverModifier(self, Filters.title("Mist Hunter"), 1));
        modifiers.add(new MayNotHaveForfeitValueReducedModifier(self,
                Filters.and(Filters.your(self), Filters.Black_Sun_agent, Filters.at(Filters.sameOrRelatedSite(self)))));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ZUCKUSS_V__DEPLOY_4LOM;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy 4-LOM from Reserve Deck");
            action.setActionMsg("Deploy non-Permanent Weapon 4-LOM here from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action,
                            Filters.and(Filters._4_LOM, Filters.not(Filters.icon(Icon.PERMANENT_WEAPON))),
                            Filters.here(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
