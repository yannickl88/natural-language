package nl.yannickl88.language;

import nl.yannickl88.language.intent.Intent;
import nl.yannickl88.language.matcher.EntityMatch;

import java.util.ArrayList;
import java.util.List;

/**
 * The Language Processor guesses the intent of a message based on it's
 * configuration.
 *
 * @see IntentMatcherLoader
 */
public class LanguageProcessor implements IntentMatcherLoadable {
    private List<IntentMatcher> intentMatchers;

    public LanguageProcessor() {
        this.intentMatchers = new ArrayList<>();
    }

    public void addIntentMatcher(IntentMatcher intent) {
        this.intentMatchers.add(intent);
    }

    /**
     * Return the intent of a message. This is based all configured intents in
     * the processor and selects the best matching Intent. If no match can be
     * found, an Intent with action "None" is returned.
     */
    public Intent getIntent(String message) {
        float score = 0.0f;
        Intent best = new Intent("None");

        for (IntentMatcher intentMatcher : intentMatchers) {
            IntentBuilder builder = new IntentBuilder();
            builder.setAction(intentMatcher.action);

            float intentScore = score(message, builder, intentMatcher);

            if (intentScore > score) {
                score = intentScore;
                best = builder.createIntent();
            }
        }

        return best;
    }

    private float score(String message, IntentBuilder intent, IntentMatcher intentMatcher) {
        float score = 0.0f;
        int count   = 0;

        for (EntityMatchable utteranceWord : intentMatcher.getWords()) {
            EntityMatch match = utteranceWord.match(message);

            if (match.score > 0 && match.hasEntity()) {
                intent.addEntity(match.getEntityType(), match.getEntity());
            }

            score += match.score;
            count++;
        }

        return score / (float) count;
    }
}