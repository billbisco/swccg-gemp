package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
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
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Insurrection (V)
 */
public class Card1240_087 extends AbstractNormalEffect {
    public Card1240_087() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Insurrection", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("Rebel insurgents and local activists are the first step in breaking the Imperial stranglehold on a world.");
        setGameText("Deploy on table. 'Battleground system' on your [Virtual Block 4] objective is treated as 'unique (•) battleground site.' When targeted by weapons, your characters of ability = 3 may use the defense value of your other character present. Once per turn, may deploy No Disintegrations! from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.ENDOR);
        addImmuneToCardTitle(Title.Alter);
    }

    /**
     * The meaning of "battleground system" on a [Virtual Block 4] objective: while its owner has Insurrection (V) on
     * table, it is treated as "unique (•) battleground site". Checked each time the filter is used.
     * @param objective the objective
     * @return the filter
     */
    public static Filter objectiveBattlegroundSystem(final PhysicalCard objective) {
        final Filter uniqueBattlegroundSite = Filters.and(Filters.unique, Filters.battleground_site);
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                boolean insurrection = !Filters.filterActive(gameState.getGame(), objective,
                        Filters.and(Filters.owner(objective.getOwner()), Filters.title("Insurrection"), Icon.LEGACY_BLOCK_4)).isEmpty();
                return (insurrection ? uniqueBattlegroundSite : Filters.battleground_system).accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
    }

    private static boolean isTargetedByWeapon(GameState gameState, PhysicalCard card) {
        WeaponFiringState weaponFiringState = gameState.getWeaponFiringState();
        if (weaponFiringState == null) {
            return false;
        }
        Collection<PhysicalCard> targets = weaponFiringState.getTargets();
        return targets != null && targets.contains(card);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        final String playerId = self.getOwner();
        Filter yourAbility3TargetedByWeapon = Filters.and(Filters.your(playerId), Filters.character, Filters.abilityEqualTo(3),
                new Filter() {
                    @Override
                    public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                        return isTargetedByWeapon(gameState, physicalCard);
                    }
                });
        // Uses the best defense value of your other characters present (that are not also being targeted).
        return Collections.<Modifier>singletonList(new DefenseValueModifier(self, yourAbility3TargetedByWeapon, null,
                new BaseEvaluator() {
                    @Override
                    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
                        float best = 0;
                        for (PhysicalCard other : Filters.filterActive(gameState.getGame(), self,
                                Filters.and(Filters.your(playerId), Filters.character, Filters.not(target), Filters.presentWith(target)))) {
                            if (!isTargetedByWeapon(gameState, other)) {
                                best = Math.max(best, modifiersQuerying.getDefenseValue(gameState, other));
                            }
                        }
                        float own = Math.max(modifiersQuerying.getAbility(gameState, target),
                                Math.max(modifiersQuerying.getManeuver(gameState, target), modifiersQuerying.getArmor(gameState, target)));
                        return Math.max(0, best - own);
                    }
                }));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__INSURRECTION_V__DEPLOY_NO_DISINTEGRATIONS;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy No Disintegrations! from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.title(Title.No_Disintegrations), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
