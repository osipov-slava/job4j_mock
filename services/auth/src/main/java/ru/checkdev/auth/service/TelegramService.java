package ru.checkdev.auth.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.checkdev.auth.domain.Profile;
import ru.checkdev.auth.exception.WrongPasswordException;
import ru.checkdev.auth.repository.PersonRepository;

@Service
@AllArgsConstructor
public class TelegramService {
    private final Logger log = LoggerFactory.getLogger(TelegramService.class);
    private final PasswordEncoder encoding = new BCryptPasswordEncoder();
    private final PersonService personService;
    private final PersonRepository personRepository;

    @Transactional
    public Object bind(Profile profile) {
        try {
            var savedProfile = personService.findByEmail(profile.getEmail());
            if (savedProfile.isEmpty()) {
                return new Object() {
                    public String getError() {
                        return "Пользователь с таким E-mail не зарегистрирован.";
                    }
                };
            }
            if (personService.findByTelegramId(profile.getTelegramId()).isPresent()) {
                return new Object() {
                    public String getError() {
                        return """
                                Данный телеграм аккаунт уже привязан к Checkdev.
                                Чтобы узнать Имя пользователя и email,
                                привязанного к данному аккаунту наберите /check""";
                    }
                };
            }
            updateTelegramId(profile, savedProfile.get().getPassword());
            return new Object() {
                public String getOk() {
                    return "ok";
                }
            };
        } catch (WrongPasswordException e) {
            log.error(e.getMessage());
            return new Object() {
                public String getError() {
                    return "Неправильный пароль";
                }
            };
        } catch (Exception e) {
            log.error(e.getMessage());
            return new Object() {
                public String getError() {
                    return "Ошибка в БД";
                }
            };
        }
    }

    @Transactional
    public Object unbind(Profile profile) {
        try {
            var savedProfile = personService.findByEmail(profile.getEmail());
            if (savedProfile.isEmpty()) {
                return new Object() {
                    public String getError() {
                        return "Пользователь с таким E-mail не зарегистрирован.";
                    }
                };
            }
            var optionalProfile = personService.findByTelegramId(profile.getTelegramId());
            if (optionalProfile.isEmpty()) {
                return new Object() {
                    public String getError() {
                        return "Данный телеграм аккаунт НЕ привязан к Checkdev.";
                    }
                };
            }
            if (!optionalProfile.get().getEmail().equals(profile.getEmail())) {
                return new Object() {
                    public String getError() {
                        return "Данный телеграм привязан к другому email";
                    }
                };
            }
            profile.setTelegramId(null);
            updateTelegramId(profile, savedProfile.get().getPassword());
            return new Object() {
                public String getOk() {
                    return "ok";
                }
            };
        } catch (WrongPasswordException e) {
            log.error(e.getMessage());
            return new Object() {
                public String getError() {
                    return "Неправильный пароль";
                }
            };
        } catch (Exception e) {
            log.error(e.getMessage());
            return new Object() {
                public String getError() {
                    return "Ошибка в БД";
                }
            };
        }
    }

    public Object check(Profile profile) {
        try {
            var optionalProfile = personService.findByTelegramId(profile.getTelegramId());
            if (optionalProfile.isEmpty()) {
                return new Object() {
                    public String getError() {
                        return "Данный телеграм аккаунт НЕ привязан к Checkdev.";
                    }
                };
            }
            return optionalProfile.get();
        } catch (Exception e) {
            log.error(e.getMessage());
            return new Object() {
                public String getError() {
                    return "Ошибка в БД";
                }
            };
        }
    }

    public void updateTelegramId(Profile profile, String encode) throws WrongPasswordException {
        if (this.encoding.matches(profile.getPassword(), encode)) {
            personRepository.updateTelegramId(profile.getEmail(), profile.getTelegramId());
        } else throw new WrongPasswordException();
    }

}
