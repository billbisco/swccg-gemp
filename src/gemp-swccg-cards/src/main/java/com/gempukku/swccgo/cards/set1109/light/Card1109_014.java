package com.gempukku.swccgo.cards.set1109.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireOneWeaponTwicePerBattleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAddBattleDestinyDrawsModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveGameTextCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 9
 * Type: Character
 * Subtype: Jedi Master
 * Title: Mace Windu
 */
public class Card1109_014 extends AbstractJediMaster {
    public Card1109_014() {
        super(Side.LIGHT, 1, 7, 6, 7, 8, "Mace Windu", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("Senior Jedi Council member who maintains rigorous adherence to the Code. Sent Qui-Gon to Naboo to accompany the Queen and learn more about the mysterious 'dark warrior'.");
        setGameText("Deploys -2 to same site as a Jedi. Mace's game text may not be canceled and he may fire one weapon twice per battle. Boba Fett may not add battle destiny draws here. Once per game, may take Blaster Deflection into hand from Reserve Deck; reshuffle. Immune to attrition.");
        addPersona(Persona.MACE);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.JEDI_COUNCIL_MEMBER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.sameSiteAs(self, Filters.Jedi)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotHaveGameTextCanceledModifier(self));
        modifiers.add(new MayFireOneWeaponTwicePerBattleModifier(self, new DuringBattleCondition(), Filters.weapon));
        modifiers.add(new MayNotAddBattleDestinyDrawsModifier(self, Filters.and(Filters.Boba_Fett, Filters.here(self))));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__MACE_WINDU__TAKE_BLASTER_DEFLECTION;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Blaster Deflection into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.title(Title.Blaster_Deflection), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
