package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.ApplyAbilityToDrawBattleDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.List;

/** Darth Vader, More Machine Than Man, Virtual Block 7.1. */
public class Card1271_002 extends AbstractImperial {
    private static final String RISE = "Rise Of The Dark Lord";

    public Card1271_002() {
        super(Side.DARK, 1, 6, 6, 6, 8, "Darth Vader, More Machine Than Man", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setLore("Dark Lord of the Sith. Servant of Emperor. Encased in armor with cybernetic life support. Student of Obi-Wan Kenobi. Was the best starpilot in the galaxy. Cunning warrior.");
        setGameText("Adds 3 to power of anything he pilots. [Vader] cards never deploy unless Rise Of The Dark Lord on table. Once per turn, if at a site you control, may apply his ability to draw battle destiny elsewhere. May not be Disarmed. Immune to Clash Of Sabers and attrition < 5.");
        addPersona(Persona.VADER);
        addIcons(Icon.VADER, Icon.PILOT, Icon.WARRIOR);
        addImmuneToCardTitle(Title.Clash_Of_Sabers);
    }

    private boolean riseMissing(SwccgGame game, PhysicalCard self) {
        return !GameConditions.canSpot(game, self, Filters.title(RISE));
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new MayNotDeployModifier(self, (state, modifiers) -> riseMissing(game, self)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition noRise = (state, modifiers) -> riseMissing(game, self);
        return List.of(new AddsPowerToPilotedBySelfModifier(self, 3),
            new MayNotDeployModifier(self, Icon.VADER, noRise, self.getOwner()),
            new ImmuneToAttritionLessThanModifier(self, 5),
            new MayNotBeDisarmedModifier(self));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        if (!GameConditions.isDuringBattleAt(game, Filters.not(Filters.here(self)))
                || !GameConditions.canSpotLocation(game, Filters.and(Filters.site, Filters.here(self), Filters.controls(playerId)))
                || !GameConditions.isOncePerTurn(game, self, playerId, sourceId)
                || game.getModifiersQuerying().getAbilityForBattleDestiny(game.getGameState(), self) <= 0) return null;

        TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, sourceId);
        action.setText("Apply Vader's ability for battle destiny");
        action.appendUsage(new OncePerTurnEffect(action));
        action.appendEffect(new ApplyAbilityToDrawBattleDestinyEffect(action, playerId, self));
        return List.of(action);
    }
}
