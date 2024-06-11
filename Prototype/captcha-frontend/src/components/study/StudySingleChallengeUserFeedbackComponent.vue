<template>
  <div class="StudyUserFeedbackComponent">
    <h1>User Feedback</h1>
    <p>This study helps to develop a new type of "Are you human?" detection
      program called "semantic NLP CAPTCHA". <br>
      Captcha in general determine if a user really is human and come in
      different forms like CheckBox Captcha or Image Captcha. </p>
    <p> For further information about Captchas see here: <a href="https://en.wikipedia.org/wiki/CAPTCHA"
                                                            target="_blank">CAPTCHA - Wikipedia</a></p>
    <p>Please provide feedback on your experience. </p>
    <h2>General Information:</h2>
    <p>Is english your mother tongue ?</p>
    <select v-model="mother_language">
      <option value="yes">Yes</option>
      <option value="no">No</option>
      <option value="">No answer</option>
    </select>

    <h2>Challenge:</h2>

    <p>How easy was it to understand the sentences in the challenge in general? <br> Rate on a scale from 1
      being impossible and 10 being trivial to understand: </p>
    <input type="number" v-model="rating_determine_sentence_understanding_difficulty">

    <p>How easy was it to rate the meaning similarity of the highlighted words? <br> Rate on a scale from 1
      being impossible and 10 being trivial to rate: </p>
    <input type="number" v-model="rating_determine_similarity_difficulty">

    <p>How convenient and practical do you think is such type of "human-detection" challenge in daily life ? <br> Rate
      on a scale from 1
      being not at all convenient or practical and 10 being highly convenient and practical:</p>
    <input type="number" v-model="challenge_feedback1">

    <p>And would you prefer that challenge over any different kind of Captcha-Challenge (like Image-Captcha)? </p>
    <textarea v-model="challenge_feedback2" v-auto-grow ></textarea>

    <p>What is your opinion on the count of items in one challenge? <br> How many would you be willing to solve without
      being majorly annoyed as a Captcha-Challenge in daily life? </p>
    <input type="number" v-model="challenge_count_opinion">

    <h2>General Feedback:</h2>
    <p>Any other feedback you would like to provide about the challenge or to improve this study?</p>
    <textarea v-model="general_feedback" v-auto-grow > </textarea>
    <p>Thank you for your participation!</p>
    <button @click="submitFeedback">Submit Feedback</button>
  </div>
</template>

<script>
export default {
  name: "StudyUserFeedbackComponent",
  data() {
    return {
      mother_language: "",

      rating_determine_sentence_understanding_difficulty: null,
      rating_determine_similarity_difficulty: null,
      challenge_feedback1: "",
      challenge_feedback2: "",
      challenge_count_opinion: null,

      general_feedback: ""
    };
  },
  methods: {
    submitFeedback() {

      // Check if all fields have been filled out
      // if (this.pair_challenge_feedback === "" ||
      //   this.pair_challenge_count_opinion === null ||
      //   this.list_challenge_feedback === "" ||
      //   this.list_challenge_count_opinion === null
      // ) {
      //   alert("Please fill out all fields before submitting.");
      //   return;
      // }


      // Create an object with all the feedback fields
      const feedback = {
        motherLanguage: this.mother_language,

        ratingSentenceUnderstanding: this.rating_determine_sentence_understanding_difficulty,
        ratingDetermineSemanticMeaning: this.rating_determine_similarity_difficulty,
        ChallengeFeedback: this.challenge_feedback1 + " | " + this.challenge_feedback2,
        ChallengeCountOpinion: this.challenge_count_opinion,

        generalFeedback: this.general_feedback
      };

      this.$emit("submitFeedback", feedback);
    }
  },
  directives: {
    autoGrow: {
      update: function (el) {
        el.style.height = 'auto';
        el.style.height = (el.scrollHeight) + 'px';
      }
    }
  }
};
</script>

<style scoped>
textarea {
  width: 50%;
  min-height: 25px;
  max-height: 200px;
  resize: vertical;
}
</style>