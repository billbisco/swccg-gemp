package com.gempukku.swccgo.cards.set1220.dark;

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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckSimultaneouslyWithCardEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NotUniqueModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Imperial War Machine
 */
public class Card1220_040 extends AbstractNormalEffect {
    public Card1220_040() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Imperial_War_Machine, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setLore("Absolute control wielded by the Emperor enables Imperial forces to dominate planetary systems before the Rebel Alliance can gain a foothold.");
        setGameText("Deploy on table. Imperial pilots are not restricted (•••). Once per turn, may reveal a non-unique Imperial pilot from hand to take a non-unique AT-AT (of armor > 6), Victory-class Star Destroyer, or Dreadnaught, (or vice versa) into hand from Reserve Deck and deploy both simultaneously; reshuffle. (Immune to Alter.)");
        addIcons(Icon.HOTH);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new NotUniqueModifier(self, Filters.and(Filters.Imperial, Filters.pilot)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final Filter armorMoreThanSixAtAt = Filters.and(Filters.AT_AT, new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().hasArmorAttribute()
                        && physicalCard.getBlueprint().getArmor() != null
                        && physicalCard.getBlueprint().getArmor() > 6;
            }
        });
        final Filter vehicleOrStarship = Filters.and(Filters.not(Filters.unique),
                Filters.or(armorMoreThanSixAtAt, Filters.Victory_class_Star_Destroyer, Filters.Dreadnaught));
        final Filter nonUniqueImperialPilot = Filters.and(Filters.Imperial, Filters.pilot, Filters.not(Filters.unique));
        Filter filter = Filters.or(nonUniqueImperialPilot, vehicleOrStarship);
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__IMPERIAL_WAR_MACHINE_V__DEPLOY_BOTH;

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.hasInHand(game, playerId, filter)
                && GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal Imperial pilot or AT-AT/Star Destroyer/Dreadnaught");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendTargeting(
                    new ChooseCardFromHandEffect(action, playerId, filter) {
                        @Override
                        protected void cardSelected(SwccgGame game, final PhysicalCard selectedCard) {
                            final Filter searchFilter;
                            if (Filters.pilot.accepts(game, selectedCard)) {
                                action.setActionMsg("Take a non-unique AT-AT (armor > 6), Victory-class Star Destroyer, or Dreadnaught from Reserve Deck and deploy both simultaneously");
                                searchFilter = vehicleOrStarship;
                            }
                            else {
                                action.setActionMsg("Take a non-unique Imperial pilot from Reserve Deck and deploy both simultaneously");
                                searchFilter = nonUniqueImperialPilot;
                            }
                            action.appendEffect(
                                    new ShowCardOnScreenEffect(action, selectedCard));
                            action.appendEffect(
                                    new DeployCardFromReserveDeckSimultaneouslyWithCardEffect(action, selectedCard, searchFilter, true));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
