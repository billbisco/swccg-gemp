package com.gempukku.swccgo.cards.set1105.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.TwicePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.CalculationVariableMultiplierModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 5: A Dark Time
 * Type: Character
 * Subtype: Alien
 * Title: Rayc Ryjerd
 */
public class Card1105_017 extends AbstractAlien {
    public Card1105_017() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Rayc Ryjerd", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("An 'honest' smuggler. Working for Jabba to pay off debts for his ship. Rycar's son. Even more of an idiot.");
        setGameText("[Pilot] 3. Twice per game, may take one Big One, Kessel Run, or Rycar's Run into hand from Reserve Deck; reshuffle. Once per game, may double the value of X for Kessel Run or Rycar's Run. Rayc is a matching pilot for any freighter or yacht.");
        addIcons(Icon.JABBAS_PALACE, Icon.PILOT);
        addKeywords(Keyword.SMUGGLER);
        addPersona(Persona.RAYC);
        setMatchingStarshipFilter(Filters.or(Filters.freighter, yacht()));
    }

    private static Filter yacht() {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                for (ModelType modelType : physicalCard.getBlueprint().getModelTypes()) {
                    if (modelType == ModelType.BAUDO_CLASS_STAR_YACHT
                            || modelType == ModelType.DLANSEAUX_STAR_YACHT
                            || modelType == ModelType.KALEVALAN_YACHT
                            || modelType == ModelType.NAUUR_CLASS_YACHT
                            || modelType == ModelType.UBRIKKIAN_LUXURY_SPACE_YACHT) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId uploadId = GameTextActionId.OVS__RAYC_RYJERD__UPLOAD_RUN;
        Filter runFilter = Filters.or(Filters.Big_One, Filters.Kessel_Run, Filters.Rycars_Run);
        if (GameConditions.isTwicePerGame(game, self, uploadId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, uploadId)) {
            final TopLevelGameTextAction upload = new TopLevelGameTextAction(self, gameTextSourceCardId, uploadId);
            upload.setText("Take card into hand from Reserve Deck");
            upload.setActionMsg("Take one Big One, Kessel Run, or Rycar's Run into hand from Reserve Deck");
            upload.appendUsage(
                    new TwicePerGameEffect(upload));
            upload.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(upload, playerId, runFilter, true));
            actions.add(upload);
        }

        GameTextActionId doubleId = GameTextActionId.OVS__RAYC_RYJERD__DOUBLE_X;
        if (GameConditions.isOncePerGame(game, self, doubleId)) {
            final TopLevelGameTextAction doubleX = new TopLevelGameTextAction(self, gameTextSourceCardId, doubleId);
            doubleX.setText("Double X for Kessel Run or Rycar's Run");
            doubleX.appendUsage(
                    new OncePerGameEffect(doubleX));
            doubleX.appendEffect(
                    new AddUntilEndOfGameModifierEffect(doubleX,
                            new CalculationVariableMultiplierModifier(self, Filters.or(Filters.Kessel_Run, Filters.Rycars_Run), 2),
                            "Doubles X for Kessel Run or Rycar's Run"));
            actions.add(doubleX);
        }

        return actions;
    }
}
