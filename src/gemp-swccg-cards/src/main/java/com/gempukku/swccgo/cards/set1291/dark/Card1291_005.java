package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.filters.LegacyFilters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.modifiers.AlignAttemptModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Character
 * Subtype: Republic
 * Title: Nute Gunray (V)
 */
public class Card1291_005 extends AbstractRepublic {
    public Card1291_005() {
        super(Side.DARK, 3, 3, 3, 4, 6, Title.Nute_Gunray, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Commanding Viceroy of the Trade Federation forces assigned to the blockade of Naboo. Takes responsibility for his own actions. Neimoidian leader.");
        setGameText("Deploys -1 to Council Room. Adds 1 to any attempt to 'align' a system. If an 'aligned' system on table, once per turn during any battle, may lose 1 Force to add 1 to your total power for each unique (•) [Separatist] character on table.");
        addIcons(Icon.SEPARATIST, Icon.EPISODE_I);
        addPersona(Persona.GUNRAY);
        addKeywords(Keyword.LEADER);
        setSpecies(Species.NEIMOIDIAN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.titleContains("Council Room")));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AlignAttemptModifier(self, Filters.system, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__NUTE_GUNRAY_V__ADD_TO_TOTAL_POWER;
        final Filter uniqueSeparatistCharacter = Filters.and(Filters.unique, Filters.character, Icon.SEPARATIST);

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canSpotLocation(game, LegacyFilters.alignedSystem)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Lose 1 Force to add to total power");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            int count = Filters.countActive(game, self, uniqueSeparatistCharacter);
                            action.appendEffect(
                                    new ModifyTotalPowerUntilEndOfBattleEffect(action, count, playerId,
                                            "Adds " + count + " to " + playerId + "'s total power"));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
