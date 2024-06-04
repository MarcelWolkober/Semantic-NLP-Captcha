<template>
  <div class="StudyStartComponent">
    <p> In this study a new type of Captcha, named "semantic NLP Captcha" will be tested. </p>
    <p> There will be <strong> no </strong> personal data collected, the participant answers and metrics ( such as mother tongue and taken
      time ) will be stored anonymously. </p>
    <p> The study will take approximately 10 minutes to complete. </p>
    <p>Notice: Try not to reload the page or move backward a page or you have to restart the Study.</p>
    <p> If your are ready press the button below. </p>
    <button id="startStudyButton" @click="startStudy"> Start Study</button>
  </div>
</template>

<script>
export default {
  name: "StudyStartComponent",
  data() {
    return {
      studyStarted: false
    };
  },
  methods: {
    startStudy() {
      this.studyStarted = true;
      this.$emit("studyStarted", true);
    }
  }
};

</script>

<style scoped>

</style>