<template>
  <div>
    <nav class="navbar navbar-expand-md navbar-dark bg-dark">
      <div class="container">
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNavDropdown"
          aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>


        <div class="collapse navbar-collapse" id="navbarNavDropdown">
          <ul class="navbar-nav mx-auto">
            <li class="nav-item">
              <a class="nav-link mx-2 active" aria-current="page"><nuxt-link
                  :to="`/clients/${this.$auth.user.username}`">Profile</nuxt-link></a>
            </li>
            <li class="nav-item">
              <a class="nav-link mx-2 active" aria-current="page"><nuxt-link :to="`registerOccurrence`">Register
                  Occurrence</nuxt-link></a>
            </li>
            <li class="nav-item">
              <a class="nav-link mx-2 active" aria-current="page"><nuxt-link :to="`myOccurrences`">See my
                  Occurrences</nuxt-link></a>
            </li>
            <li class="nav-item">
              <a class="nav-link mx-2 active" aria-current="page"><nuxt-link :to="`myPolicies`">See my
                  Policies</nuxt-link></a>
            </li>
            <li class="nav-item">
              <a class="nav-link mx-2 active" aria-current="page"><nuxt-link :to="`registerPolicy`">Register
                  Policy</nuxt-link></a>
            </li>
            <li class="nav-item">
              <a class="nav-link mx-2 btn btn-danger" @click.prevent="signOut">Sign Out</a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <div class="mx-auto" style="width: 600px; margin-top: 50px;">
      <div class="mt-3" style="text-align: center;">
        <a><b>Register a Policy</b></a>
      </div>
      <div>
        <form @submit.prevent="submit">
          <div class="form-group">
            <label for="exampleFormControlInput1">Type</label>
            <input type="text" class="form-control" id="exampleFormControlInput1" placeholder="E.g. Phone"
              :state="hasType" v-model="type">
          </div>
          <div class="form-group">
            <label for="exampleFormControlInput1">Covers</label>
            <input type="text" class="form-control" id="exampleFormControlInput1" placeholder="E.g. Broken Screen"
              :state="hasCovers" v-model="covers">
          </div>
          <div class="form-group">
            <label for="exampleFormControlInput1">Insurance</label>
            <b-form-select v-model="insurance" :options="options"></b-form-select>
            <div class="mt-3">Selected: <strong>{{ insurance }}</strong></div>
          </div>
          <div class="mt-3" style="text-align: center; padding: 50px;">
            <b-button type="submit">Register</b-button>
          </div>

        </form>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
export default {
  data() {
    return {
      options: [
        { value: null, text: 'Please select an option' },
        { value: 'Fidelidade', text: 'Fidelidade' },
        { value: 'Teleseguros', text: 'Teleseguros' },
        { value: 'SegurosSA', text: 'SegurosSA' },
        { value: 'SegurosPortugal', text: 'SegurosPortugal' },
        { value: 'GlobalInsurance', text: 'GlobalInsurance' }
      ],
      type: null,
      covers: null,
      insurance: null,
      occurrence: {
        client: {},
      }
    }
  },
  computed: {
    username() {
      return this.$route.params.username
    },
  },

  methods: {
    signOut() {
      this.$auth.logout()
      this.$router.push('/')
    },
    submit() {
      if (this.type == null) {
        this.$toast.error('Please insert a type.').goAway(3000)
        return
      }
      if (this.covers == null) {
        this.$toast.error('Please specify what the policy covers.').goAway(3000)
        return
      }
      if (this.insurance == null) {
        this.$toast.error('Please specify the insurance company.').goAway(3000)
        return
      }

      let promisse = this.$axios.$post('/api/policies/',
        {
          "client": this.$route.params.username,
          "type": this.type,
          "covers": this.covers,
          "insurance": this.insurance
        }
      )
      promisse.then(() => {
        this.$toast.success('Policy submitted!').goAway(3000)
        this.$router.push('myPolicies')
      })
      promisse.catch(() => {
        this.$toast
          .error('Sorry, error submitting policy!')
          .goAway(3000)
      })
    }
  }
}
</script>
