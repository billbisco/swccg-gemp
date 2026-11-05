package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayFireOnlyOncePerBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.2
 * Type: Effect
 * Title: The Camp (V)
 */
public class Card1272_026 extends AbstractNormalEffect {
    public Card1272_026() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.The_Camp, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("An isolated camp protected by Obi-Wan and Captain Panaka was established at the Queen's starship while Qui-Gon searched for a way to repair the leaking hyperdrive.");
        setGameText("Unless your [Reflections 2] objective on table, deploy on table. If [Permanent Weapon] Maul in battle at a site, and you have no unique (•) character of ability > 3 (except Luke of ability = 4) there, lightsabers there may not be 'swung' twice. Once per game, may take one K'lor'slug or a [Setup] card into hand from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.EPISODE_I, Icon.TATOOINE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return !GameConditions.canSpot(game, self, Filters.and(Filters.your(playerId), Filters.Objective, Icon.REFLECTIONS_II));
    }

    /** [Permanent Weapon] Maul in battle at a site, and you have no unique character of ability > 3 (except Luke of ability = 4) there. */
    static Condition maulInBattleWithoutYourUniqueAbilityFour(final PhysicalCard self) {
        final Filter permanentWeaponMaul = Filters.and(Filters.Maul, Icon.PERMANENT_WEAPON, Filters.participatingInBattle);
        final Filter yourUniqueAbilityFour = Filters.and(Filters.your(self), Filters.unique, Filters.character, Filters.abilityMoreThan(3),
                Filters.not(Filters.and(Filters.Luke, Filters.abilityEqualTo(4))), Filters.at(Filters.battleLocation));
        return new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                PhysicalCard battleLocation = gameState.getBattleLocation();
                if (battleLocation == null || !Filters.site.accepts(gameState, modifiersQuerying, battleLocation))
                    return false;
                return GameConditions.canSpot(gameState.getGame(), self, permanentWeaponMaul)
                        && !GameConditions.canSpot(gameState.getGame(), self, yourUniqueAbilityFour);
            }
        };
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter lightsabersThere = Filters.and(Filters.or(Filters.lightsaber, Filters.hasPermanentWeapon(Filters.lightsaber)), Filters.at(Filters.battleLocation));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayFireOnlyOncePerBattleModifier(self, lightsabersThere, maulInBattleWithoutYourUniqueAbilityFour(self)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__THE_CAMP_V__TAKE_CARD;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take K'lor'slug or [Setup] card into hand");
            action.setActionMsg("Take one K'lor'slug or a [Setup] card into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.title(Title.Klorslug), Icon.SETUP), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
