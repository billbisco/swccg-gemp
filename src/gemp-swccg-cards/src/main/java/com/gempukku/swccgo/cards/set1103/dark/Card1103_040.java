package com.gempukku.swccgo.cards.set1103.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 3: Revolution
 * Type: Character
 * Subtype: Imperial
 * Title: Motti (V)
 */
public class Card1103_040 extends AbstractImperial {
    public Card1103_040() {
        super(Side.DARK, 1, 3, 3, 3, 5, "Motti", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Senior Navy Commander of Death Star. Believes in technology. Ridiculed the Force. Ambitious leader. Promoted due to support of New Order, not military skills. Hates Vader.");
        setGameText("Adds 2 to power of anything he pilots. Unique (•) Star Destroyers are power +2 (+5 if armed with a starship weapon). Kuat Drive Yards is immune to Alter. Once per game, may deploy Kuat Drive Yards from Reserve Deck; reshuffle.");
        addPersona(Persona.MOTTI);
        addIcons(Icon.A_NEW_HOPE, Icon.PILOT, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter uniqueStarDestroyers = Filters.and(Filters.unique, Filters.Star_Destroyer);
        Filter armedUniqueStarDestroyers = Filters.and(uniqueStarDestroyers, Filters.armedWith(Filters.starship_weapon));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new PowerModifier(self, uniqueStarDestroyers, 2));
        modifiers.add(new PowerModifier(self, armedUniqueStarDestroyers, 3));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Kuat_Drive_Yards, Title.Alter));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__MOTTI__DEPLOY_KUAT_DRIVE_YARDS;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Kuat_Drive_Yards)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Kuat Drive Yards from Reserve Deck");
            action.setActionMsg("Deploy Kuat Drive Yards from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Kuat_Drive_Yards, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
